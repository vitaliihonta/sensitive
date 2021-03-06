import scala.util.matching.Regex

package object sensitive {
  sealed trait MaskedTag[T] extends Any

  type Masked[A] = A with MaskedTag[A]

  private[sensitive] def Masked[A](value: A): Masked[A] =
    value.asInstanceOf[Masked[A]]

  def sensitiveOf[A]: SensitiveBuilder[A] =
    new SensitiveBuilder[A](transform = identity[A])

  @`inline` final implicit def Sensitive[A](self: A): SensitiveOps[A] =
    new SensitiveOps[A](self)

  def substitute[A](by: A): ParameterMasking[A] =
    ParameterMasking.substitute(by)

  def regexp(pattern: Regex, replacement: String): ParameterMasking[String] =
    ParameterMasking.regexp(pattern, replacement)
}
