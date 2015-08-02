package robots.protocol.exclusion

import scala.util.matching.Regex

/**
 * Set containing rules for a specific agent in a robotstxt file. If there are
 * patterns that both explicitly allow and explicitly disallow a path, the one
 * with greater priority is applied (the priority is equal to the length of the
 * originating path pattern from the robotstxt file).
 *
 * @author andrei
 */
final class RuleSet private (
    allowedPaths: Seq[RuleSet.Pattern],
    disallowedPaths: Seq[RuleSet.Pattern],
    crawlDelay: Double) {
  /**
   * Checks whether the given path is allowed.
   */
  println(allowedPaths)
  println(disallowedPaths)
  def isAllowed(path: String): Boolean = {
    val allowed = allowedPaths.find(_.matches(path)).map(_.priority)
    val disallowed = disallowedPaths.find(_.matches(path)).map(_.priority)
    allowed.getOrElse(-1) >= disallowed.getOrElse(-1)
  }

  /**
   * Checks whether the given path is disallowed.
   */
  def isDisallowed(path: String): Boolean = !isAllowed(path)

  /**
   * Returns crawl-delay in milliseconds or 0 if not specified.
   */
  def delayInMs: Int = (1000 * crawlDelay).toInt
}

/**
 * Factory object for the [[robots.protocol.exclusion.RuleSet]] class.
 *
 * @author andrei
 */
object RuleSet {
  private final case class Pattern private (regex: Regex, priority: Int) {
    def matches(string: String): Boolean = string match {
      case regex(_*) => true
      case _ => false
    }
  }

  private object Pattern {
    def apply(pattern: String): Pattern = {
      val dollar = if (pattern.last == '$') "$" else ""
      val p = if (dollar == "$") pattern.dropRight(1) else pattern + "*"
      val regex =
        ("\\Q" + p.split("\\*", -1).mkString("\\E.*?\\Q") + "\\E" + dollar).r
      val priority = pattern.length
      Pattern(regex, priority)
    }

    implicit def ordering: Ordering[Pattern] =
      Ordering.by(e => (-e.priority, e.regex.toString()))
  }

  /**
   * Creates a [[robots.protocol.exclusion.RuleSet]] from a dictionary of
   * directives.
   */
  def apply(directives: Map[Directive, Seq[String]]): RuleSet = {
    val allow = directives.getOrElse(Allow, Seq.empty[String])
      .distinct.map((p: String) => Pattern(p)).sorted
    val disallow = directives.getOrElse(Disallow, Seq.empty[String])
      .distinct.map((p: String) => Pattern(p)).sorted
    val delay = directives.getOrElse(CrawlDelay, Seq("0")).map(_.toDouble).max
    new RuleSet(allow, disallow, delay)
  }

  /**
   * Returns empty [[robots.protocol.exclusion.RuleSet]] (equivalent to an empty
   * or absent robotstxt file).
   */
  def empty: RuleSet = RuleSet(Map.empty[Directive, Seq[String]])
}