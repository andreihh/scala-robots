package robots.protocol.exclusion

import org.scalatest.FunSuite

import scala.io.Source

/**
 * @author andrei
 */
class RobotstxtParserTests extends FunSuite {
  def getRobotstxtContent(name: String): String = {
    val stream = getClass.getResourceAsStream("/" + name)
    Source.fromInputStream(stream).getLines().mkString("\n")
  }

  def parse(content: String): Robotstxt = {
    RobotstxtParser(content)
  }

  def getRobotstxt(name: String): Robotstxt = parse(getRobotstxtContent(name))

  test("dmoz.org/robots.txt") {
    val name = "dmoz.txt"
    val robotstxt = getRobotstxt(name)
    assert(robotstxt.delayInMs("HHbot") == 1000)
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
    assert(robotstxt.delayInMs("HHbot") == 5500)
    assert(robotstxt.sitemaps == Seq("/sitemap.xml"))
    assert(robotstxt.isAllowed("HHbot2", "/problema/text/teste"))
    assert(robotstxt.isDisallowed("HHbot", "/problema/trenuri"))
    assert(robotstxt.isDisallowed("HHbot2", "/problema/text/edit"))
  }
}
