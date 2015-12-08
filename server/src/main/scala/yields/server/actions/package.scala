package yields.server

import java.net.URL

import com.rometools.rome.io.{SyndFeedInput, XmlReader}

import scala.util.Try

package object actions {

  private lazy val mailPattern = """\S+@\S+\.\S+""".r
  private lazy val urlPattern = """https?://\S+.\S+?""".r

  /**
    * Check whether an email is valid.
    * @param mail email to test
    * @return true if valid
    */
  def validEmail(mail: String): Boolean =
    mailPattern.unapplySeq(mail).nonEmpty

  /**
    * Check whether an url is valid
    * @param url url to test
    * @return true if valid
    */
  def validURL(url: String): Boolean =
    urlPattern.unapplySeq(url).nonEmpty

  /**
    * Check whether an RSS is valid
    * @param rss url to test
    * @return true if valid
    */
  def validRSS(rss: String): Boolean =
    Try(new SyndFeedInput().build(new XmlReader(new URL(rss)))).isSuccess

}
