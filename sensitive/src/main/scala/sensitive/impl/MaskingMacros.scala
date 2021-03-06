package sensitive.impl

import sensitive.ParameterMasking
import sensitive.Sensitive
import sensitive.SensitiveBuilder
import scala.reflect.macros.blackbox

class MaskingMacros(override val c: blackbox.Context) extends MacroUtils(c) {

  import c.universe._

  private def libraryUsageValidityCheck[A: WeakTypeTag](): Unit =
    if (!(c.prefix.tree.tpe =:= weakTypeOf[SensitiveBuilder[A]])) {
      error("Invalid library usage! Refer to documentation")
    }

  def sensitiveFieldImpl[A: WeakTypeTag, B: WeakTypeTag](f: Expr[A => B]): Tree = {
    val SensitiveBuilder = weakTypeOf[SensitiveBuilder[A]].dealias
    val A                = weakTypeOf[A].dealias
    val B                = weakTypeOf[B].dealias

    libraryUsageValidityCheck[A]()

    val transformation = extractSelectorField(f.tree)
      .map { field =>
        val sensitive = findImplicit(weakTypeOf[Sensitive[B]], s"value $field of type $B is not sensitive")
        q""" (value: $A) => value.copy($field = $sensitive.masked(value.$field)) """
      }
      .getOrElse(
        error(s"Expected a field selector to be passed (as instance.field1), got $f")
      )

    q"""new $SensitiveBuilder(${c.prefix.tree}, $transformation)"""
  }

  def maskImpl[A: WeakTypeTag, B: WeakTypeTag](f: Expr[A => B])(masking: Expr[ParameterMasking[B]]): Tree = {
    val SensitiveBuilder = weakTypeOf[SensitiveBuilder[A]].dealias
    val A                = weakTypeOf[A].dealias

    libraryUsageValidityCheck[A]()

    val transformation = extractSelectorField(f.tree)
      .map(field => q""" (value: $A) => value.copy($field = $masking(value.$field)) """)
      .getOrElse(
        error(s"Expected a field selector to be passed (as instance.field1), got $f")
      )

    q"""new $SensitiveBuilder(${c.prefix.tree}, $transformation)"""
  }
}
