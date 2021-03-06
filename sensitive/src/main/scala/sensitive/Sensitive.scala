package sensitive

import scala.annotation.implicitNotFound

@implicitNotFound("${A} is not sensitive")
trait Sensitive[A] {
  def masked(value: A): Masked[A]
}

class SensitiveOps[A](private val self: A) extends AnyVal {
  def masked(implicit sensitive: Sensitive[A]): A = sensitive.masked(self)
}
