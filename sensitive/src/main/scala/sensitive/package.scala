import scala.util.matching.Regex

package object sensitive {
  sealed trait MaskedTag[T]         extends Any
  sealed trait AsMaskedStringTag[T] extends Any

  type Masked[A] = A with MaskedTag[A]

  type AsMaskedString[A] = String with AsMaskedStringTag[A]

  @internalApi def Masked[A](value: A): Masked[A] =
    value.asInstanceOf[Masked[A]]

  @internalApi def AsMaskedString[A](value: String): AsMaskedString[A] =
    value.asInstanceOf[AsMaskedString[A]]

  @`inline` final def sensitiveOf[A <: Product]: SensitiveBuilder[A] =
    new SensitiveBuilder[A](Set.empty, Map.empty)

  @`inline` final implicit def Sensitive[A](self: A): SensitiveOps[A] =
    new SensitiveOps[A](self)

  def substitute[A](by: A): ParameterMasking[A] =
    new ParameterMasking.Substitute(by)

  def regexp(pattern: Regex, replacement: RegexReplacement): ParameterMasking[String] =
    new ParameterMasking.Regexp(pattern, replacement)

  def replaceAll(by: String): RegexReplacement = new RegexReplacement.All(by)

  def replaceAll(replacer: Regex.Match => String): RegexReplacement = new RegexReplacement.AllFunc(replacer)
}
