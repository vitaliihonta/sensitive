package sensitive.impl

import sensitive.ParameterMasking
import sensitive.ProductSensitive
import sensitive.Sensitive
import sensitive.SensitiveBuilder
import sensitive.SensitiveBuilderTransformation

import scala.reflect.macros.blackbox

class MaskingMacros(override val c: blackbox.Context) extends MacroUtils(c) {

  import c.universe._

  private def libraryUsageValidityCheck[A <: Product: WeakTypeTag](): Unit = {
    if (!(c.prefix.tree.tpe =:= weakTypeOf[SensitiveBuilder[A]])) {
      error("Invalid library usage! Refer to documentation")
    }
    val A           = weakTypeOf[A].dealias
    val tpe         = A.typeSymbol
    val isCaseClass = tpe.isClass && tpe.asClass.isCaseClass
    if (!isCaseClass) {
      error(s"Expected $A to be a case class")
    }
  }

  def buildImpl[A <: Product: WeakTypeTag]: Tree = {
    val A                = weakTypeOf[A].dealias
    val ProductSensitive = weakTypeOf[ProductSensitive[A]].dealias

    libraryUsageValidityCheck[A]()

    locally {

      val caseAccessors = A.decls.collect {
        case m: MethodSymbol if m.isCaseAccessor => m.asMethod
      }.toList

      val transformationsTree = getTransformations[A](c.prefix.tree)

      val nameTransforms = caseAccessors.map(_.name.toString).zipWithIndex.toMap

      val name                = freshTermName("name")
      val value               = freshTermName("value")
      val transformationsName = freshTermName("transformations")
      val transformation      = freshTermName("transformation")
      val nameIndices         = freshTermName("nameIndices")
      val idx                 = freshTermName("idx")

      val transform = {
        val transformations = caseAccessors.map { field =>
          val name = field.name.toString
          val tpe = field.returnType.dealias
          q"$field = $transformationsName.get($name).fold($value.$field)(_.transformField($value.$field).asInstanceOf[$tpe])"
        }
        q""" ($value: $A) => $value.copy(..$transformations) """
      }

      val maskedNames =
        q"""
          val $nameIndices = $nameTransforms
          $transformationsName.map {
            case ($name, $transformation) =>
              val $idx = $nameIndices($name)
              $idx -> $transformation.maskField
          }
          """

      q"""
         val $transformationsName = $transformationsTree
         new $ProductSensitive($transform, $maskedNames)
      """ debugged "build"
    }
  }

  def sensitiveFieldImpl[A <: Product: WeakTypeTag, B: WeakTypeTag](f: Expr[A => B]): Tree = {
    val SensitiveBuilder               = weakTypeOf[SensitiveBuilder[A]].dealias
    val SensitiveBuilderTransformation = weakTypeOf[SensitiveBuilderTransformation[B]].dealias
    val A                              = weakTypeOf[A].dealias
    val B                              = weakTypeOf[B].dealias

    libraryUsageValidityCheck[A]()

    val transformations = getTransformations[A](c.prefix.tree)

    val copyValue = freshTermName("copyValue")
    val maskValue = freshTermName("maskValue")

    val (fieldName, transform) = extractSelectorField(f.tree)
      .map { field =>
        val sensitive = findImplicit(weakTypeOf[Sensitive[B]], s"value $field of type $B is not sensitive")
        val transform =
          q"""
            SensitiveBuilderTransformation(
              transformField = ($copyValue: $B) => $sensitive.masked($copyValue),
              maskField = ($maskValue: $B) => $sensitive.asMaskedString($maskValue)
            ).asInstanceOf[SensitiveBuilderTransformation[Any]]"""

        field.toString -> transform
      }
      .getOrElse(
        error(s"Expected a field selector to be passed (as instance.field1), got $f")
      )

    q"""new $SensitiveBuilder($transformations + ($fieldName -> $transform))""" //debugged "withFieldSensitive"
  }

  def maskImpl[A <: Product: WeakTypeTag, B: WeakTypeTag](f: Expr[A => B])(masking: Expr[ParameterMasking[B]]): Tree = {
    val SensitiveBuilder               = weakTypeOf[SensitiveBuilder[A]].dealias
    val SensitiveBuilderTransformation = weakTypeOf[SensitiveBuilderTransformation[B]].dealias
    val A                              = weakTypeOf[A].dealias
    val B                              = weakTypeOf[B].dealias

    libraryUsageValidityCheck[A]()

    val transformations = getTransformations[A](c.prefix.tree)

    val copyValue = freshTermName("copyValue")
    val maskValue = freshTermName("maskValue")

    val (fieldName, transform) = extractSelectorField(f.tree)
      .map { field =>
        val transform =
          q"""
            SensitiveBuilderTransformation(
              transformField = ($copyValue: $B) => $masking($copyValue),
              maskField = ($maskValue: $B) => $masking($maskValue).toString
            ).asInstanceOf[SensitiveBuilderTransformation[Any]]"""

        field.toString -> transform
      }
      .getOrElse(
        error(s"Expected a field selector to be passed (as instance.field1), got $f")
      )

    q"""new $SensitiveBuilder($transformations + ($fieldName -> $transform))""" //debugged "withFieldMasked"
  }

  private def getTransformations[A <: Product: WeakTypeTag](tree: Tree): Tree = {
    val SensitiveBuilder = weakTypeOf[SensitiveBuilder[A]].dealias

    tree match {
      case q"sensitive.`package`.sensitiveOf[$a]" =>
        reify(Map.empty[String, SensitiveBuilderTransformation[Any]]).tree

      case Typed(Apply(Select(New(tree), _), List(arg)), tpeTree)
          if tpeTree.tpe =:= SensitiveBuilder && tree.tpe =:= SensitiveBuilder =>
        arg
    }
  }
}
