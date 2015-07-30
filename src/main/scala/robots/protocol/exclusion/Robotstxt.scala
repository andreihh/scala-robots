package robots.protocol.exclusion

import scala.util.Try

/**
 * Represents the contents of a robotstxt file. Internally, the data is kept as
 * a map from user-agents to [[robots.protocol.exclusion.RuleSet]] and a
 * sequence of sitemaps.
 *
 * @param sitemaps Sequence containing all sitemaps listed in this robotstxt
 *
 * @author andrei
 */
final class Robotstxt private (
    agentRules: Map[String, RuleSet],
    val sitemaps: Seq[String]) {
  /**
   * The user-agent that matches all robots but has the least priority.
   */
  def wildcardAgent: String = "*"

  /**
   * Returns the [[robots.protocol.exclusion.RuleSet]] that applies to the given
   * `agent`.
   */
  def getRules(agent: String): RuleSet = agentRules
    .get(agent)
    .orElse(agentRules.get(wildcardAgent))
    .getOrElse(RuleSet.empty)

  /**
   * Checks if the given `path` is allowed for the given `agent`.
   */
  def isAllowed(agent: String, path: String): Boolean =
    getRules(agent).isAllowed(path)

  /**
   * Checks if the given `path` is disallowed for the given `agent`.
   */
  def isDisallowed(agent: String, path: String): Boolean =
    getRules(agent).isDisallowed(path)

  /**
   * Returns the crawl delay for the given `agent` in milliseconds.
   */
  def delayInMs(agent: String): Double = getRules(agent).delayInMs
}

/**
 * Factory object for the [[robots.protocol.exclusion.Robotstxt]] class.
 */
object Robotstxt {
  private def agentRules(directives: Seq[(Directive, String)]) = {
    type Group = (Seq[(Directive, String)], Seq[(Directive, String)])

    def getGroup(d: Seq[(Directive, String)]): Group = {
      def isUserAgent(d: (Directive, String)): Boolean = d match {
        case (UserAgent, _) => true
        case _ => false
      }

      val agents = d.takeWhile(isUserAgent)
      val groupDirectives = d.drop(agents.length).takeWhile(!isUserAgent(_))
      (agents, groupDirectives)
    }

    def makeGroups(d: Seq[(Directive, String)]): Seq[Group] = {
      if (d.isEmpty) Nil
      else {
        val (agents, groupDirectives) = getGroup(d)
        (agents, groupDirectives) +:
          makeGroups(d.drop(agents.length + groupDirectives.length))
      }
    }

    val groups = makeGroups(directives)
    groups.foldLeft(Map.empty[String, Seq[(Directive, String)]])({
      case (map, (agents, group)) =>
        map ++ agents.map({ case (agent, name) => name -> group })
    }).mapValues(directiveSeq => directiveSeq
        .groupBy({ case (d, v) => d }).mapValues(_.map { case (d, v) => v }))
      .mapValues(RuleSet(_))
  }

  private def validDirective(directive: (Directive, String)) = directive match {
    case (_, "") => false
    case (UserAgent, _) => true
    case (CrawlDelay, value) => Try(value.toDouble >= 0.0).getOrElse(false)
    case (Allow, _) => true
    case (Disallow, _) => true
    case (Sitemap, _) => true
    case (Unkown(_), _) => false
  }

  private def sitemaps(directives: Seq[(Directive, String)]) = for {
    (d, v) <- directives
    if d == Sitemap
  } yield v

  /**
   * Builds a [[robots.protocol.exclusion.Robotstxt]] from parsed directives. It
   * is important to feed as argument directly the output from the
   * [[robots.protocol.exclusion.RobotstxtParser]], as it only returns the
   * parsed directives in order, but does not establish groups of rules
   * according to the robotstxt format. This task belongs to this factory
   * object.
   *
   * @param directives Parsed directives from a raw string
   * @return Resulting [[robots.protocol.exclusion.Robotstxt]]
   */
  def apply(directives: Seq[(Directive, String)]): Robotstxt = {
    val validDirectives = directives.filter(validDirective)
    new Robotstxt(agentRules(validDirectives), sitemaps(validDirectives))
  }
}
