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
    Source.fromInputStream(stream).getLines().mkString("\n")
  }

  def parse(content: String): Try[Robotstxt] = RobotstxtParser(content)

  def getRobotstxt(name: String): Robotstxt =
    parse(getRobotstxtContent(name)).get

  test("dmoz.org/robots.txt") {
    val name = "dmoz.txt"
    val robotstxt = getRobotstxt(name)
    assert(robotstxt.getRules("HHbot").delayInMs == 1000)
  }

  test("github.com/robots.txt") {
    val name = "github.txt"
    val robotstxt = getRobotstxt(name)
  }

  test("google.com/robots.txt") {
    val name = "google.txt"
    val robotstxt = getRobotstxt(name)
    assert(robotstxt.sitemaps.nonEmpty)
  }

  test("reddit.com/robots.txt") {
    val name = "reddit.txt"
    val robotstxt = getRobotstxt(name)
  }

  test("wikipedia.org/robots.txt") {
    val name = "wikipedia.txt"
    val robotstxt = getRobotstxt(name)
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
