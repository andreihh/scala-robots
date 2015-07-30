package robots.protocol.exclusion

import scala.util.parsing.combinator.RegexParsers

/**
 * Parser for robotstxt files from raw strings.
 *
 * @author andrei
 */
object RobotstxtParser extends RegexParsers {
  override protected val whiteSpace = """(\s|#.*+)+""".r

  private val value: Parser[String] = """([\w\Q-.~:/?#[]@!$&'()*+,;=\E]*+)""".r

  private val directive: Parser[~[Directive, String]] = {
    val parsers = Directive.supportedDirectives.map(d => regex(d.regex.r)) :+
      regex(Unkown.regex.r)
    ((parsers.reduce(_ | _) ^^ { case Directive(d) => d }) <~ ":") ~ value
  }

  private val content: Parser[Seq[(Directive, String)]] =
    directive.* ^^ (_.map { case d ~ v => d -> v })

  private val rules: Parser[Robotstxt] = content ^^ (Robotstxt(_))

  /**
   * Parses the `input` string and returns the resulting
   * [[robots.protocol.exclusion.Robotstxt]], or throws an exception if the
   * parse fails.
   */
  def apply(input: String): Robotstxt = parse(rules, input).get
}