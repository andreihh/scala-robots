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

/**
 * Abstract representation of possible robots meta-tags in html documents. If a
 * new tag is added, it should be appended to the
 * [[robots.protocol.exclusion.html.Tag.supportedTags]] sequence.
 */
sealed abstract class Tag {
  /**
   * Returns a regex that allows identifying the tag from the `name` attribute
   * of the meta-tag.
   */
  def nameRegex: String
}

case object All extends Tag {
  def nameRegex: String = "[aA][lL][lL]"
}

case object None extends Tag {
  def nameRegex: String = "[nN][oO][nN][eE]"
}

case object Follow extends Tag {
  def nameRegex: String = "[fF][oO][lL][lL][oO][wW]"
}

case object NoFollow extends Tag {
  def nameRegex: String = "[nN][oO][fF][oO][lL][lL][oO][wW]"
}

case object Index extends Tag {
  def nameRegex: String = "[iI][nN][dD][eE][xX]"
}

case object NoIndex extends Tag {
  def nameRegex: String = "[nN][oO][iI][nN][dD][eE][xX]"
}

/**
 * Companion object for [[robots.protocol.exclusion.html.Tag]] class.
 */
object Tag {
  /**
   * Returns a sequence containing all supported meta-tags.
   */
  def supportedTags: Seq[Tag] =
    Seq(All, None, Follow, NoFollow, Index, NoIndex)

  require(supportedTags.forall {
    case All => true
    case None => true
    case Follow => true
    case NoFollow => true
    case Index => true
    case NoIndex => true
  })

  /**
   * Returns a sequence of tags from a raw string.
   */
  def parse(content: String): Seq[Tag] =
    content.split("""[^\w]""")
      .flatMap(name => supportedTags.find(tag => name.matches(tag.nameRegex)))
}