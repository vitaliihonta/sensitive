package sensitive

import scala.util.matching.Regex

trait RegexReplacement {
  def replace(regex: Regex, text: String): String
}

object RegexReplacement {

  private[sensitive] class All(replacement: String) extends RegexReplacement {
    override def replace(regex: Regex, text: String): String = regex.replaceAllIn(text, replacement)
  }

  private[sensitive] class AllFunc(replace: Regex.Match => String) extends RegexReplacement {

    override def replace(regex: Regex, text: String): String =
      regex.replaceAllIn(text, replace)
  }

}
