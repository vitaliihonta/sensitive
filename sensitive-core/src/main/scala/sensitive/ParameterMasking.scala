package sensitive

import scala.util.matching.Regex

trait ParameterMasking[A] {
  def apply(value: A): Masked[A]
}

object ParameterMasking {
  def noop[A]: ParameterMasking[A] = (value: A) => Masked(value)

  def substitute[A](by: A): ParameterMasking[A] = (_: A) => Masked(by)

  def regexp(pattern: Regex, replacement: String): ParameterMasking[String] =
    (value: String) => Masked(pattern.replaceAllIn(value, replacement))
}
