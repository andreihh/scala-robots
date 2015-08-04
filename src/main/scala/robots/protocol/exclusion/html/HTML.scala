package robots.protocol.exclusion.html

import java.io.ByteArrayInputStream

import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import org.xml.sax.InputSource

import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter

/**
 * Object used for parsing raw html pages to xml.
 *
 * @author andrei
 */
object HTML {
  private val adapter = new NoBindingFactoryAdapter()
  private val parser = (new SAXFactoryImpl).newSAXParser

  /**
   * Returns [[scala.xml.Node]] obtained from parsing this html saved as a
   * string with a specific encoding (by default UTF-8).
   */
  def parse(html: String, encoding: String = "UTF-8"): Node =
    parse(html.getBytes(encoding))

  /**
   * Returns [[scala.xml.Node]] obtained from parsing this html saved as a byte
   * array.
   */
  def parse(html: Array[Byte]): Node = {
    val stream = new ByteArrayInputStream(html)
    val source = new InputSource(stream)
    adapter.loadXML(source, parser)
  }
}
