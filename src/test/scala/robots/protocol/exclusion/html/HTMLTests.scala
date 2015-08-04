package robots.protocol.exclusion.html

import org.scalatest.FunSuite

import java.net.URL

import scala.io.Source

/**
 * @author andrei
 */
class HTMLTests extends FunSuite {
  def getContent(name: String): String = {
    val stream = getClass.getResourceAsStream("/" + name)
    val content = Source.fromInputStream(stream).getLines().mkString("\n")
    content
  }

  def same(first: Seq[Tag], second: Seq[Tag]): Boolean = {
    first.forall(second.contains) && second.forall(first.contains)
  }

  test("Sample page test") {
    val name = "sample.html"
    val page = Page(new URL("http://www.sample.com/"), getContent(name))
    assert(same(page.metaTags("HHbot"), Seq(NoIndex, NoFollow)))
    assert(same(page.metaTags("HHbot2"), Seq(NoIndex, Follow)))
    assert(same(page.metaTags("nobody"), Seq(All, None, Follow, NoIndex)))
  }

  test("Wikipedia main page test") {
    val name = "wikipedia.html"
    val page =
      Page(new URL("https://en.wikipedia.org/wiki/Main_Page"), getContent(name))
    val outlinks = page.outlinks.map(_.toURI)
    val check = Seq(
      new URL("https://en.wikipedia.org/wiki/Wikipedia"),
      new URL("https://en.wikipedia.org/wiki/Free_content"),
      new URL("https://en.wikipedia.org/wiki/Portal:Biography"),
      new URL("https://en.wikipedia.org/wiki/Horse_breed"),
      new URL("https://en.wikipedia.org/wiki/Tanaka_Yoshio")
    ).map(_.toURI)
    assert(check.forall(outlinks.contains))
  }
}
