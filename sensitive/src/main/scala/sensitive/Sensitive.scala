package sensitive

import scala.annotation.implicitNotFound

@implicitNotFound("${A} is not sensitive")
trait Sensitive[A] extends BaseSensitive[A] {
  def masked(value: A): Masked[A]

  def asMaskedString(value: A): AsMaskedString[A]

  override def maskBase(value: A): A = masked(value)

  override def maskedStringBase(value: A): String = asMaskedString(value)
}

class SensitiveOps[A](private val self: A) extends AnyVal {
  def masked(implicit sensitive: Sensitive[A]): Masked[A] = sensitive.masked(self)

  def asMaskedString(implicit sensitive: Sensitive[A]): AsMaskedString[A] = sensitive.asMaskedString(self)
}

abstract class ToStringMasked[A: Sensitive] { self: A =>
  override def toString: String = (self: A).asMaskedString
}
