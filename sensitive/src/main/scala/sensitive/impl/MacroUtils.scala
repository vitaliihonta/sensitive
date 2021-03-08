package sensitive.impl

import scala.reflect.macros.TypecheckException
import scala.reflect.macros.blackbox

abstract class MacroUtils(val c: blackbox.Context) {
  import c.universe._

  def extractSelectorField(t: Tree): Option[TermName] =
    t match {
      case q"(${vd: ValDef}) => ${idt: Ident}.${fieldName: TermName}" if vd.name == idt.name =>
        Some(fieldName)
      case _ =>
        None
    }

  def findImplicit(tpe: Type, errorMessage: => String): Tree =
    try c.inferImplicitValue(tpe, silent = false)
    catch {
      case _: TypecheckException =>
        error(errorMessage)
    }

  def freshTermName(name: String): TermName = c.freshName(TermName(name))

  def error(message: String): Nothing = c.abort(c.enclosingPosition, message)

  protected implicit class Debugged[A](self: A) {

    def debugged(msg: String): A = {
      println(s"$msg: $self")
      self
    }
  }
}
