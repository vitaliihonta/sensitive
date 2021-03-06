package sensitive.derivation

case class Derived[A](value: A) extends AnyVal

trait LowPriorityDerived {
  implicit def mkDerived[A](implicit ev: A): Derived[A] = Derived(ev)
}
