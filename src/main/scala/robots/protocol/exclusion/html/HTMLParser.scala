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

import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import org.xml.sax.InputSource

import java.io.ByteArrayInputStream

import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter

/**
 * Object used for parsing raw html pages to xml.
 */
object HTMLParser {
  private val adapter = new NoBindingFactoryAdapter()
  private val parser = (new SAXFactoryImpl).newSAXParser

  /**
   * Returns [[scala.xml.Node]] obtained from parsing this html saved as a
   * string with a specific encoding (by default UTF-8).
   */
  def apply(html: String, encoding: String = "UTF-8"): Node =
    apply(html.getBytes(encoding))

  /**
   * Returns [[scala.xml.Node]] obtained from parsing this html saved as a byte
   * array.
   */
  def apply(html: Array[Byte]): Node = {
    val stream = new ByteArrayInputStream(html)
    val source = new InputSource(stream)
    adapter.loadXML(source, parser)
  }
}
