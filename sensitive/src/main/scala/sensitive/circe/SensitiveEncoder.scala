package sensitive.circe

import io.circe.Encoder
import io.circe.Json
import sensitive.Sensitive
import sensitive.derivation.Derived

class SensitiveEncoder[A: Sensitive](implicit ev: Derived[Encoder[A]]) extends Encoder[A] {
  override def apply(a: A): Json = ev.value.apply(a.masked)
}
