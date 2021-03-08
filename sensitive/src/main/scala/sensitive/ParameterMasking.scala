package sensitive

import scala.util.matching.Regex

trait ParameterMasking[A] extends BaseSensitive[A] {
  def apply(value: A): Masked[A]

  override def maskBase(value: A): A = apply(value)

  override def maskedStringBase(value: A): String = apply(value).toString

}

object ParameterMasking {

  class Noop[A] extends ParameterMasking[A] {
    override def apply(value: A): Masked[A] = Masked(value)

    override def toString: String = "ParameterMasking.Noop"
  }

  class Substitute[A](by: A) extends ParameterMasking[A] {
    override def apply(value: A): Masked[A] = Masked(by)

    override def toString: String = s"ParameterMasking.Substitute($by)"
  }

  class Regexp(pattern: Regex, replacement: RegexReplacement) extends ParameterMasking[String] {
    override def apply(value: String): Masked[String] = Masked(replacement.replace(pattern, value))

    override def toString: String = s"ParameterMasking.Regexp($pattern, $replacement)"
  }
}
