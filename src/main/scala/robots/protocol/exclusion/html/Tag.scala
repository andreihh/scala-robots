package robots.protocol.exclusion.html

/**
 * Abstract representation of possible robots meta-tags in html documents. If a
 * new tag is added, it should be appended to the
 * [[robots.protocol.exclusion.html.Tag.supportedTags]] sequence.
 *
 * @author Andrei Heidelbacher
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