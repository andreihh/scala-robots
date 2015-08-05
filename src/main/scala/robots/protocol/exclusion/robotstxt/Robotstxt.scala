package robots.protocol.exclusion.robotstxt

import scala.util.Try

/**
 * Represents the contents of a robotstxt file. Internally, the data is kept as
 * a map from user-agents to [[robots.protocol.exclusion.robotstxt.RuleSet]] and
 * a sequence of sitemaps.
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
   * Returns all user-agents mentioned in this robotstxt.
   */
  def userAgents: Seq[String] = agentRules.keys.toSeq

  /**
   * Returns the [[robots.protocol.exclusion.robotstxt.RuleSet]] that applies to
   * the given `agent`.
   */
  def getRules(agent: String): RuleSet = agentRules
    .get(agent)
    .orElse(agentRules.get(wildcardAgent))
    .getOrElse(RuleSet.empty)
}

/**
 * Factory object for the [[robots.protocol.exclusion.robotstxt.Robotstxt]]
 * class.
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
   * Builds a [[robots.protocol.exclusion.robotstxt.Robotstxt]] from parsed
   * directives. The directives should appear in the same order as in the
   * robotstxt file to build the correct rules.
   *
   * @param directives Parsed directives from a raw string
   * @return Resulting [[robots.protocol.exclusion.robotstxt.Robotstxt]]
   */
  def apply(directives: Seq[(Directive, String)]): Robotstxt = {
    val validDirectives = directives.filter(validDirective)
    new Robotstxt(agentRules(validDirectives), sitemaps(validDirectives))
  }

  /**
   * Returns a [[robots.protocol.exclusion.robotstxt.Robotstxt]] parsed from the
   * given byte array. The content must be a UTF-8 encoded string.
   */
  def apply(content: Array[Byte]): Robotstxt =
    RobotstxtParser(new String(content, "UTF-8")).getOrElse(empty)

  /**
   * Returns a [[robots.protocol.exclusion.robotstxt.Robotstxt]] parsed from the
   * given string. The content must be UTF-8 encoded.
   */
  def apply(content: String): Robotstxt =
    RobotstxtParser(content).getOrElse(empty)

  /**
   * Returns the equivalent of an empty robotstxt file.
   */
  def empty: Robotstxt = apply(Nil)
}
