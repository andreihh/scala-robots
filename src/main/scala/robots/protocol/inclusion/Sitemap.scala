package robots.protocol.inclusion

import java.net.URL

import scala.util.Try
import scala.xml.XML

/**
 * Abstract class that represents a sitemap.
 *
 * @author andrei
 */
sealed abstract class Sitemap {
  /**
   * The location (URL) of the sitemap.
   */
  val location: URL

  /**
   * All valid links at which the sitemap points to. A link is valid if it has
   * correct URL syntax and points to a page placed somewhere inside the
   * directory where the sitemap is located. It filters the URLs returned by the
   * `parseLinks` method.
   */
  final val links: Seq[URL] = {
    val rootDirectory = location.toString.reverse.dropWhile(_ != '/').reverse

    def makeURLs(urls: Seq[String]): Seq[URL] =
      urls.flatMap(url => Try(new URL(url)).toOption)

    def validURL(url: URL): Boolean =
      url.toString.startsWith(rootDirectory)

    makeURLs(parseLinks).filter(validURL)
  }

  /**
   * Returns all raw URLs parsed from the sitemap.
   */
  protected def parseLinks: Seq[String]
}

final class SitemapIndex(val location: URL, content: String) extends Sitemap {
  protected def parseLinks: Seq[String] =
    (XML.loadString(content) \\ "sitemap" \ "loc").map(_.text)
}

final class SitemapXML(val location: URL, content: String) extends Sitemap {
  protected def parseLinks: Seq[String] =
    (XML.loadString(content) \\ "url" \ "loc").map(_.text)
}

final class SitemapRSS(val location: URL, content: String) extends Sitemap {
  protected def parseLinks: Seq[String] =
    (XML.loadString(content) \\ "item" \ "link").map(_.text)
}

final class SitemapTXT(val location: URL, content: String) extends Sitemap {
  protected def parseLinks: Seq[String] = content.split("""\s""")
}

/**
 * Factory object for the [[robots.protocol.inclusion.Sitemap]] class.
 */
object Sitemap {
  /**
   * Automatically recognizes the sitemap format by returning the one which
   * returns the most valid links. If a new sitemap format is added, it should
   * also be updated in this method.
   *
   * @param location URL of the sitemap
   * @param content Raw string content of the sitemap
   * @return Concrete [[robots.protocol.inclusion.Sitemap]] automatically
   * identified according to the raw content.
   */
  def apply(location: URL, content: String): Sitemap = {
    val index = Try(new SitemapIndex(location, content))
    val xml = Try(new SitemapXML(location, content))
    val rss = Try(new SitemapRSS(location, content))
    val txt = Try(new SitemapTXT(location, content))
    val sitemaps = Seq(index, xml, rss, txt)
    sitemaps.flatMap(_.toOption).maxBy(_.links.length)
  }
}