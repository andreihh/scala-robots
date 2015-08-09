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

import java.net.URL

import scala.util.Try
import scala.xml.Node

/**
 * Representation of a HTML page at a given url.
 *
 * @param url Location of the page
 */
final class Page private (val url: URL, content: Node) {
  private def getMetaTags(robotName: String): Option[Seq[Tag]] = {
    val metaTags = for {
      tag <- content \ "head" \ "meta"
      name = tag \ "@name"
      if name.text == robotName
      content <- tag \ "@content"
      value <- Tag.parse(content.text)
    } yield value
    if (metaTags.nonEmpty) Option(metaTags) else Option.empty[Seq[Tag]]
  }

  /**
   * Returns the default robot name to be specified in the meta-tags.
   */
  def defaultRobotName: String = "robots"

  /**
   * Returns the robot meta tags for the given `robotName`.
   */
  def metaTags(robotName: String): Seq[Tag] =
    getMetaTags(robotName)
      .orElse(getMetaTags(defaultRobotName))
      .getOrElse(Seq(All))

  /**
   * Parsed `outlinks` from the HTML document.
   */
  val outlinks: Seq[URL] = {
    val links = (content \\ "a").map(link => (link \ "@href").text)
    links.flatMap(href => Try(new URL(url, href)).toOption)
  }
}

/**
 * Factory object for [[robots.protocol.exclusion.html.Page]].
 */
object Page {
  /**
   * Returns a page parsed from the `content` and located at the given `url`.
   */
  def apply(url: URL, content: String, encoding: String = "UTF-8"): Page =
    new Page(url, HTMLParser(content, encoding))

  /**
   * Returns a page parsed from the `content` and located at the given `url`.
   */
  def apply(url: URL, content: Array[Byte]): Page =
    new Page(url, HTMLParser(content))
}