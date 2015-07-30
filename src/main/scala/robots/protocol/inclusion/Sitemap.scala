package robots.protocol.inclusion

import java.net.URL

import scala.util.Try
import scala.xml.XML

/**
 * @author andrei
 */
sealed abstract class Sitemap {
  val location: URL

  val links: Seq[URL]
}

final class SitemapXML(val location: URL, content: String) extends Sitemap {
  val links: Seq[URL] = {
    val urls = (XML.loadString(content) \ "url" \ "loc").map(_.text)
    Sitemap.filterURLs(location, urls)
  }
}

final class SitemapTxt(val location: URL, content: String) extends Sitemap {
  val links: Seq[URL] = {
    val urls = content.split("\\s")
    Sitemap.filterURLs(location, urls)
  }
}

object Sitemap {
  def apply(location: URL, content: String): Sitemap = {
    val xml = Try(new SitemapXML(location, content))
    val txt = Try(new SitemapTxt(location, content))
    val sitemaps = Seq(xml, txt)
    sitemaps.filter(_.isSuccess).map(_.get).maxBy(_.links.length)
  }

  def makeURLs(urls: Seq[String]): Seq[URL] =
    urls.map(url => Try(new URL(url))).filter(_.isSuccess).map(_.get)

  def validURL(sitemap: URL, url: URL): Boolean = {
    def getRootDirectory(location: URL) =
      location.toString.reverse.dropWhile(_ != '/').reverse

    url.toString.startsWith(getRootDirectory(sitemap))
  }

  def filterURLs(sitemap: URL, urls: Seq[String]): Seq[URL] =
    makeURLs(urls).filter(validURL(sitemap, _))
}