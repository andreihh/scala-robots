package robots.protocol.exclusion

import scala.util.parsing.combinator.RegexParsers
import scala.util.Try

/**
 * Parser for robotstxt files from raw strings.
 *
 * @author andrei
 */
object RobotstxtParser extends RegexParsers {
  override protected val whiteSpace = """(\s|#.*+)+""".r

  private val directiveValue: Parser[String] =
    """: *+([\w\Q-.~:/?#[]@!$&'()*+,;=\E]*+)""".r ^^ { matched =>
      matched.tail.dropWhile(_ == ' ')
    }

  private val directive: Parser[~[Directive, String]] = {
    val parsers = Directive.supportedDirectives.map(d => regex(d.regex.r)) :+
      regex(Unkown.regex.r)
    (parsers.reduce(_ | _) ^^ { case Directive(d) => d }) ~ directiveValue
  }

  private val content: Parser[Seq[(Directive, String)]] =
    directive.* ^^ (_.map { case d ~ v => d -> v })

  private val rules: Parser[Robotstxt] = content ^^ (Robotstxt(_))

  /**
   * Parses the `input` string and returns a 'Try' containing the resulting
   * [[robots.protocol.exclusion.Robotstxt]], or the exception which caused the
   * parse to fail.
   */
  def apply(input: String): Try[Robotstxt] = Try(parse(rules, input).get)
}