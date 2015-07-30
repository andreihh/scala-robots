package robots.protocol.exclusion

/**
 * Abstract representation for a supported directive. All supported directives
 * must be implemented as an object that extends this class.
 *
 * @author andrei
 */
sealed abstract class Directive {
  /**
   * Returns a regex that allows parsing the directive.
   */
  def regex: String
}

case object UserAgent extends Directive {
  def regex = """[uU][sS][eE][rR]-[aA][gG][eE][nN][tT]"""
}

case object Allow extends Directive {
  def regex = """[aA][lL][lL][oO][wW]"""
}

case object Disallow extends Directive {
  def regex = """[dD][iI][sS][aA][lL][lL][oO][wW]"""
}

case object CrawlDelay extends Directive {
  def regex = """[cC][rR][aA][wW][lL]-[dD][eE][lL][aA][yY]"""
}

case object Sitemap extends Directive {
  def regex = """[sS][iI][tT][eE][mM][aA][pP]"""
}

/**
 * Abstract class to represent unsupported directives.
 *
 * @param name String representation of the encountered directive
 */
final case class Unkown(name: String) extends Directive {
  def regex = Unkown.regex
}

/**
 * Companion object for [[robots.protocol.exclusion.Unkown]] class that allows
 * parsing the robotstxt file without failing when it encounters an unkown
 * directive.
 */
object Unkown {
  def regex = """[\w-]*+"""
}

/**
 * Companion object for the [[robots.protocol.exclusion.Directive]] class
 * containing utilities for parsing.
 */
object Directive {
  /**
   * Returns a sequence with all supported directives. If a new directive is
   * added, it must be added in this sequence and the `require` should be
   * updated. This assures that adding a directive will be dealt with in all
   * required places at the first run.
   */
  def supportedDirectives: Seq[Directive] =
    Seq(UserAgent, Allow, Disallow, CrawlDelay, Sitemap)

  require(supportedDirectives.forall {
    case UserAgent => true
    case Allow => true
    case Disallow => true
    case CrawlDelay => true
    case Sitemap => true
    case Unkown(_) => false
  })

  /**
   * Extractor to allow parsers identify a directive from a raw string.
   */
  def unapply(name: String): Option[Directive] =
    (supportedDirectives :+ Unkown(name)).find(d => name.matches(d.regex))
}