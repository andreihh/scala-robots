package robots.protocol.exclusion.robotstxt

import scala.util.matching._
import scala.util.parsing.combinator.RegexParsers
import scala.util.Try

/**
 * Parser for robotstxt files from raw strings.
 *
 * @author Andrei Heidelbacher
 */
object RobotstxtParser extends RegexParsers {
  override protected val whiteSpace = """(\s|#.*+)+""".r

  private def regexMatch(r: Regex): Parser[Regex.Match] =
    new Parser[Regex.Match] {
      def apply(in: Input) = {
        val source = in.source
        val offset = in.offset
        val start = handleWhiteSpace(source, offset)
        r.findPrefixMatchOf(source.subSequence(start, source.length)) match {
          case Some(matched) =>
            Success(matched, in.drop(start + matched.end - offset))
          case None =>
            Failure("string matching regex `" + r+ "' expected but `" +
              in.first + "' found", in.drop(start - offset))
        }
      }
    }

  private val directiveValue: Parser[String] =
    regexMatch(""": *+(.*+)""".r) ^^ {
      _.group(1).replaceAll(" ++#.*+", "").trim
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
   * [[robots.protocol.exclusion.robotstxt.Robotstxt]], or the exception which
   * caused the parse to fail.
   */
  def apply(input: String): Try[Robotstxt] = Try(parse(rules, input).get)
}