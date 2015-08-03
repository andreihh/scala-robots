package robots.protocol.exclusion

import org.scalatest.FunSuite

import scala.io.Source
import scala.util.Try

/**
 * @author andrei
 */
class RobotstxtParserTests extends FunSuite {
  def getRobotstxtContent(name: String): String = {
    val stream = getClass.getResourceAsStream("/" + name)
    val content = Source.fromInputStream(stream).getLines().mkString("\n")
    content
  }

  def parse(content: String): Try[Robotstxt] = RobotstxtParser(content)

  def getRobotstxt(name: String): Robotstxt =
    parse(getRobotstxtContent(name)).get

  test("dmoz.org/robots.txt") {
    val name = "dmoz.txt"
    val robotstxt = getRobotstxt(name)
    val rules = robotstxt.getRules("HHbot")
    assert(rules.delayInMs == 1000)
  }

  test("github.com/robots.txt") {
    val name = "github.txt"
    val robotstxt = getRobotstxt(name)
    val rules = robotstxt.getRules("CCBot")
    assert(rules
      .isAllowed("/andrei-heidelbacher/scala-robots/tree/master/src"))
    assert(rules
      .isDisallowed("/andrei-heidelbacher/scala-robots/graphs/contributors"))
  }

  test("google.com/robots.txt") {
    val name = "google.txt"
    val robotstxt = getRobotstxt(name)
    assert(Seq("http://www.google.com/sitemaps_webmasters.xml",
        "https://www.google.com/edu/sitemap.xml",
        "https://www.google.com/work/sitemap.xml",
        "http://www.google.com/hostednews/sitemap_index.xml",
        "http://www.google.com/maps/views/sitemap.xml"
      ).forall(robotstxt.sitemaps.contains))
  }

  test("reddit.com/robots.txt") {
    val name = "reddit.txt"
    val robotstxt = getRobotstxt(name)
    val rules = robotstxt.getRules("HHbot")
    assert(rules.isDisallowed("/.rss"))
    assert(rules.isDisallowed("/favicon.ico"))
    assert(rules.isAllowed("/some-page.jso"))
  }

  test("wikipedia.org/robots.txt") {
    val name = "wikipedia.txt"
    val robotstxt = getRobotstxt(name)
    val rules = robotstxt.getRules("HHbot")
    assert(rules.isAllowed("/w/load.php?"))
    assert(rules.isDisallowed("/w/somepage"))
  }

  test("Edge cases") {
    val name = "edge-cases.txt"
    val robotstxt = getRobotstxt(name)
    assert(robotstxt.getRules("HHbot").delayInMs == 5500)
    assert(robotstxt.sitemaps == Seq("/sitemap.xml"))
    assert(robotstxt.getRules("HHbot2").isAllowed("/problema/text/teste"))
    assert(robotstxt.getRules("HHbot").isDisallowed("/problema/trenuri"))
    assert(robotstxt.getRules("HHbot2").isDisallowed("/problema/text/edit"))
  }
}
