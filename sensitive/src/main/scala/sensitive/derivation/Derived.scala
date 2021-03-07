package sensitive.derivation

case class Derived[A](value: A) extends AnyVal

object Derived extends LowPriorityDerived

sealed trait LowPriorityDerived {
  implicit def mkDerived[A](implicit ev: A): Derived[A] = Derived(ev)
}
