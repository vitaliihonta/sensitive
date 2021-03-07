package sensitive

import io.circe.Encoder
import sensitive.derivation.Derived
import scala.annotation.implicitAmbiguous

package object circe {

  @implicitAmbiguous(
    """
Unable to derive sensitive Encoder[${A}]
In most cases it means that the default Encoder[${A}]
and sensitive.phobos import are both in the lexical scope.
Try moving the default Encoder[${A}] into implicit scope (e.g. companion object)
or defining sensitive encoder explicitly:
implicit val theSensitiveEncoder = sensitive.circe.sensitiveEncoder[${A}]

"""
  ) @inline final implicit def sensitiveEncoder[A: Sensitive](implicit ev: Derived[Encoder[A]]): SensitiveEncoder[A] =
    new SensitiveEncoder[A]
}
