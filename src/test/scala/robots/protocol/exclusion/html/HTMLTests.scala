/*
 * Copyright 2015 Andrei Heidelbacher <andrei.heidelbacher@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package robots.protocol.exclusion.html

import org.scalatest.FunSuite

import java.net.URL

import scala.io.Source

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
