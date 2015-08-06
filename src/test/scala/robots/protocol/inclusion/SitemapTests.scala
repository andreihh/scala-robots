package robots.protocol.inclusion

import org.scalatest.FunSuite

import java.net.URL

import scala.io.Source

/**
 * @author Andrei Heidelbacher
 */
class SitemapTests extends FunSuite {
  def getSitemapContent(name: String): String = {
    val stream = getClass.getResourceAsStream("/" + name)
    Source.fromInputStream(stream).getLines().mkString("\n")
  }

  test("sitemapindex.xml") {
    val name = "sitemapindex.xml"
    val content = getSitemapContent(name)
    val location = new URL("http://www.example.com/sitemap.xml")
    val links = Sitemap(location, content).links.map(_.toString)
    assert(links.sorted ==
      Seq("http://www.example.com/",
        "http://www.example.com/catalog?item=12&desc=vacation_hawaii",
        "http://www.example.com/catalog?item=73&desc=vacation_new_zealand",
        "http://www.example.com/catalog?item=74&desc=vacation_newfoundland",
        "http://www.example.com/catalog?item=83&desc=vacation_usa").sorted
    )
  }

  test("sitemap.xml") {
    val name = "sitemap.xml"
    val content = getSitemapContent(name)
    val location = new URL("http://www.example.com/sitemap.xml")
    val links = Sitemap(location, content).links.map(_.toString)
    assert(links.sorted ==
      Seq("http://www.example.com/",
        "http://www.example.com/catalog?item=12&desc=vacation_hawaii",
        "http://www.example.com/catalog?item=73&desc=vacation_new_zealand",
        "http://www.example.com/catalog?item=74&desc=vacation_newfoundland",
        "http://www.example.com/catalog?item=83&desc=vacation_usa").sorted
    )
  }

  test("sitemap.rss") {
    val name = "sitemap.rss"
    val content = getSitemapContent(name)
    val location = new URL("http://www.example.com/sitemap.rss")
    val links = Sitemap(location, content).links.map(_.toString)
    assert(links.sorted ==
      Seq("http://www.example.com/page1.html",
        "http://www.example.com/page2.html").sorted
    )
  }

  test("sitemap.txt") {
    val name = "sitemap.txt"
    val content = getSitemapContent(name)
    val location = new URL("http://www.example.com/sitemap.txt")
    val links = Sitemap(location, content).links.map(_.toString)
    assert(links.sorted ==
      Seq("http://www.example.com/",
        "http://www.example.com/catalog?item=12&desc=vacation_hawaii",
        "http://www.example.com/catalog?item=73&desc=vacation_new_zealand",
        "http://www.example.com/catalog?item=74&desc=vacation_newfoundland",
        "http://www.example.com/catalog?item=83&desc=vacation_usa").sorted
    )
  }
}
