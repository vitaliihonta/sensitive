package sensitive.impl

import sensitive.BaseSensitive
import sensitive.ParameterMasking
import sensitive.ProductSensitive
import sensitive.Sensitive
import sensitive.SensitiveBuilder
import scala.reflect.macros.blackbox

class MaskingMacros(override val c: blackbox.Context) extends MacroUtils(c) {

  import c.universe._

  private def libraryUsageValidityCheck[A: WeakTypeTag](): Unit = {
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

  def buildImpl[A: WeakTypeTag]: Tree = {
    val A                = weakTypeOf[A].dealias
    val ProductSensitive = weakTypeOf[ProductSensitive[A]].dealias

    libraryUsageValidityCheck[A]()

    val caseAccessorsWithNames = A.decls.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        val field = m.asMethod
        field -> freshTermName(s"transform_${field.name}")
    }.toList

    val (transformationsValNames, transformationsTree) = getTransformationsWithValNames[A]

    val value           = freshTermName("value")
    val transformations = freshTermName("transformations")
    val sc              = freshTermName("sc")

    val transform = {
      val copies = caseAccessorsWithNames.flatMap {
        case (field, valName) =>
          val name = field.name.toString
          val tpe  = field.returnType.dealias
          if (transformationsValNames contains name)
            Some(q"$field = $transformations($name).maskBase($value.$field).asInstanceOf[$tpe]")
          else None
      }
      q""" $value.copy(..$copies) """
    }

    val maskString = {
      val maskTransformations = caseAccessorsWithNames.zipWithIndex.map {
        case ((field, valName), idx) =>
          val name = field.name.toString
          val shown =
            if (transformationsValNames contains name) q"$transformations($name).maskedStringBase($value.$field)"
            else q"$value.$field.toString"

          val base = q"$sc.append($shown)"
          if (idx == 0) base else q"""$sc.append(','); $base"""
      }
      q"""
          val $sc = new StringBuilder
          $sc.append(${A.typeSymbol.name.toString})
          $sc.append('(')
          ..$maskTransformations
          $sc.append(')')
          $sc.toString
          """
    }

    q"""
         new $ProductSensitive {
           private val $transformations = $transformationsTree
           override def masked($value: $A): _root_.sensitive.Masked[$A] = 
             _root_.sensitive.Masked($transform)

           override def asMaskedString($value: $A): _root_.sensitive.AsMaskedString[$A] =
            _root_.sensitive.AsMaskedString($maskString)
         }
      """ debugged "[DEBUG] [sensitive] Generated instance"
  }

  def sensitiveFieldImpl[A: WeakTypeTag, B: WeakTypeTag](f: Expr[A => B]): Tree = {
    val SensitiveBuilder = weakTypeOf[SensitiveBuilder[A]].dealias
    val B                = weakTypeOf[B].dealias

    libraryUsageValidityCheck[A]()

    val (valNames, transformations) = getTransformationsTree[A]

    val (fieldName, transform) = extractSelectorField(f.tree)
      .map { field =>
        val sensitive = findImplicit(weakTypeOf[Sensitive[B]], s"value $field of type $B is not sensitive")

        field.toString -> sensitive
      }
      .getOrElse(
        error(s"Expected a field selector to be passed (as instance.field1), got $f")
      )

    q"""new $SensitiveBuilder($valNames + $fieldName, $transformations + ($fieldName -> $transform.asAny))"""
  }

  def maskImpl[A: WeakTypeTag, B: WeakTypeTag](f: Expr[A => B])(masking: Expr[ParameterMasking[B]]): Tree = {
    val SensitiveBuilder = weakTypeOf[SensitiveBuilder[A]].dealias

    libraryUsageValidityCheck[A]()

    val (valNames, transformations) = getTransformationsTree[A]

    val (fieldName, transform) = extractSelectorField(f.tree)
      .map(field => field.toString -> masking)
      .getOrElse(
        error(s"Expected a field selector to be passed (as instance.field1), got $f")
      )

    q"""new $SensitiveBuilder($valNames + $fieldName, $transformations + ($fieldName -> $transform.asAny))"""
  }

  private def getTransformationsWithValNames[A: WeakTypeTag]: (Set[String], Tree) = {
    val (valNamesTree, tree) = getTransformationsTree[A]
    val treeReset            = c.untypecheck(valNamesTree.duplicate)
    val expr                 = c.Expr[Set[String]](treeReset)
    c.eval(expr) -> tree
  }

  private def getTransformationsTree[A: WeakTypeTag]: (Tree, Tree) = {
    val SensitiveBuilder = weakTypeOf[SensitiveBuilder[A]].dealias

    c.prefix.tree match {
      case Typed(Apply(Select(New(tree), _), List(valNames, arg)), tpeTree)
          if tpeTree.tpe =:= SensitiveBuilder && tree.tpe =:= SensitiveBuilder =>
        (valNames, arg) debugged "[DEBUG] [sensitive] Selected tree from SensitiveBuilder"

      case _ =>
        reify(Set.empty[String]).tree -> reify(Map.empty[String, BaseSensitive[Any]]).tree
    }
  }
}
