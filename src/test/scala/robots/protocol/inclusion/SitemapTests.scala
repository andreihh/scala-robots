package robots.protocol.inclusion

import org.scalatest.FunSuite

import java.net.URL

import scala.io.Source

/**
 * @author andrei
 */
class SitemapTests extends FunSuite {
  def getSitemapContent(name: String): String = {
    val stream = getClass.getResourceAsStream("/" + name)
    Source.fromInputStream(stream).getLines().mkString("\n")
  }

  test("sitemap.xml") {
    val name = "sitemap.xml"
    val content = getSitemapContent(name)
    val location = new URL("http://www.example.com/sitemap.xml")
    println(new SitemapXML(location, content).links)
  }

  test("sitemap.txt") {
    val name = "sitemap.txt"
    val content = getSitemapContent(name)
    val location = new URL("http://www.example.com/sitemap.txt")
    println(new SitemapTxt(location, content).links)
  }
}
