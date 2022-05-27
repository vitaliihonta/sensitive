package sensitive

import io.circe.Encoder
import sensitive.derivation.Derived

package object circe {

  @inline final implicit def sensitiveEncoder[A: Sensitive](implicit ev: Derived[Encoder[A]]): SensitiveEncoder[A] =
    new SensitiveEncoder[A]
}
