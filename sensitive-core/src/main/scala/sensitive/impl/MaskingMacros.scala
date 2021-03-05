package sensitive.impl

import sensitive.ParameterMasking
import sensitive.SensitiveBuilder

import scala.reflect.macros.blackbox

object MaskingMacros {

  def maskImpl[A: c.WeakTypeTag, B: c.WeakTypeTag](
    c:       blackbox.Context
  )(f:       c.Expr[A => B]
  )(masking: c.Expr[ParameterMasking[B]]
  ): c.Tree = {
    import c.universe._
    val SensitiveBuilder = weakTypeOf[SensitiveBuilder[A]].dealias
    val A                = weakTypeOf[A].dealias

    if (!(c.prefix.tree.tpe =:= weakTypeOf[SensitiveBuilder[A]])) {
      c.abort(
        c.enclosingPosition,
        "Invalid library usage! Refer to documentation"
      )
    }

    def extractSelectorField(t: Tree): Option[TermName] =
      t match {
        case q"(${vd: ValDef}) => ${idt: Ident}.${fieldName: TermName}" if vd.name == idt.name =>
          Some(fieldName)
        case _ =>
          None
      }

//    println(s"prefix: ${c.prefix.tree}")
//    println(c.prefix.tree.tpe)

//    println(s"Get field: $f")
//    println(s"Masking: $masking")

    val transformation = extractSelectorField(f.tree)
      .map(field => q""" (value: $A) => value.copy($field = $masking(value.$field)) """)
      .getOrElse(
        c.abort(
          c.enclosingPosition,
          s"Expected a field selector to be passed (as instance.field1), got $f"
        )
      )

    val result =
      q"""new $SensitiveBuilder(${c.prefix.tree}, $transformation)"""
    println(result)
    result
  }
}
