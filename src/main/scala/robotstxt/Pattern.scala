package robotstxt

import scala.util.matching.Regex

/**
 * Pattern that matches relative URLs in robotstxt files. It supports the
 * wildcard `*` and the end-of-string `$`.
 *
 * @author andrei
 */
final case class Pattern private (regex: Regex, priority: Int) {
  /**
   * Checks whether the given string matches the pattern.
   */
  def matches(string: String): Boolean = string match {
    case regex(_*) => true
    case _ => false
  }
}

/**
 * Factory object for the [[Pattern]] class.
 *
 * @author andrei
 */
object Pattern {
  /**
   * Builds pattern from given string. All characters except `*` and a single
   * `$` at the end of the string are escaped.
   */
  def apply(pattern: String): Pattern = {
    val dollar = if (pattern.last == '$') "$" else ""
    val p = if (dollar == "$") pattern.dropRight(1) else pattern + "*"
    val regex =
      ("\\Q" + p.split("\\*", -1).mkString("\\E.*?\\Q") + "\\E" + dollar).r
    val priority = pattern.length
    Pattern(regex, priority)
  }

  /**
   * Compares the priorities (lengths of the originating strings) of `this` and
   * `that`, and in case of equality, lexicographically compares the associated
   * regexes.
   */
  implicit def ordering[A <: Pattern]: Ordering[A] =
    Ordering.by(e => (-e.priority, e.regex.toString()))
}
