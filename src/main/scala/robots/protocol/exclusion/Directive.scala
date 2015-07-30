package robots.protocol.exclusion

/**
 * @author andrei
 */
sealed abstract class Directive {
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

final case class Unkown(name: String) extends Directive {
  def regex = Unkown.regex
}

object Unkown {
  def regex = """[\w-]*+"""
}

object Directive {
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

  def unapply(name: String): Option[Directive] =
    (supportedDirectives :+ Unkown(name)).find(d => name.matches(d.regex))
}