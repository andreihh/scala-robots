package robotstxt

/**
 * Set containing rules for a specific agent in a robotstxt file. Supports the
 * `Allow`, `Disallow`, `Crawl-delay` directives. If there are patterns that
 * both explicitly allow and explicitly disallow a path, the one with greater
 * priority is applied (the priority is equal to the length of the originating
 * path pattern from the robots.txt file).
 *
 * @author andrei
 */
final class RuleSet private (
    allowedPaths: Seq[Pattern],
    disallowedPaths: Seq[Pattern],
    crawlDelay: Double) {
  /**
   * Checks whether the given path is allowed.
   */
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
   * @return Crawl-delay in milliseconds or 0 if not specified
   */
  def delayInMs: Int = (1000 * crawlDelay).toInt
}

/**
 * Factory object for the [[RuleSet]] class.
 *
 * @author andrei
 */
object RuleSet {
  /**
   * Creates a [[RuleSet]] from a dictionary of directives.
   */
  def apply(directives: Map[Directive, Seq[String]]): RuleSet = {
    val allow = directives.getOrElse(Allow, Seq[String]())
      .distinct.map((p: String) => Pattern(p)).sorted
    val disallow = directives.getOrElse(Disallow, Seq[String]())
      .distinct.map((p: String) => Pattern(p)).sorted
    val delay = directives.getOrElse(CrawlDelay, Seq("0")).map(_.toDouble).max
    new RuleSet(allow, disallow, delay)
  }

  /**
   * @return Empty [[RuleSet]] (equivalent to an empty or absent robotstxt file)
   */
  def empty: RuleSet = RuleSet(Map.empty[Directive, Seq[String]])
}