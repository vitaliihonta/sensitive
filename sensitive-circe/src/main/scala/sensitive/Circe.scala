package sensitive

import io.circe.Encoder
import sensitive.derivation.Derived
import sensitive.derivation.LowPriorityDerived

object Circe extends LowPriorityDerived {

  implicit def sensitiveEncoder[A: Sensitive](implicit ev: Derived[Encoder[A]]): Encoder[A] =
    ev.value.contramap[A](_.masked)
}
