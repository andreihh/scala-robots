package robotstxt

import scala.util.Try

/**
 * @author andrei
 */
class Robotstxt private (
    agentRules: Map[String, RuleSet],
    val sitemaps: Seq[String]) {
  def wildcardAgent: String = "*"

  def getRules(agent: String): RuleSet = agentRules
    .get(agent)
    .orElse(agentRules.get(wildcardAgent))
    .getOrElse(RuleSet.empty)

  def isAllowed(agent: String, path: String): Boolean =
    getRules(agent).isAllowed(path)

  def isDisallowed(agent: String, path: String): Boolean =
    getRules(agent).isDisallowed(path)

  def delayInMs(agent: String): Double = getRules(agent).delayInMs
}

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
    case (CrawlDelay, value) => Try(value.toDouble >= 0.0).getOrElse(false)
    case (Unkown(_), _) => false
    case _ => true
  }

  private def sitemaps(directives: Seq[(Directive, String)]) = for {
    (d, v) <- directives
    if d == Sitemap
  } yield v

  def apply(directives: Seq[(Directive, String)]): Robotstxt = {
    val validDirectives = directives.filter(validDirective)
    new Robotstxt(agentRules(validDirectives), sitemaps(validDirectives))
  }
}
