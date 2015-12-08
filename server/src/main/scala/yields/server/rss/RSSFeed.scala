package yields.server.rss

import java.net.URL
import java.time.OffsetDateTime

import com.rometools.rome.io.{SyndFeedInput, XmlReader}
import yields.server.utils.Temporal

import scala.collection.JavaConverters._

/**
  * Takes care of parsing a valid RSS feed and offer information retrieval manner.
  * RSS example: http://stackoverflow.com/feeds
  *
  * @param url RSS url
  */
final class RSSFeed(url: String) {

  private val feed = new SyndFeedInput().build(new XmlReader(new URL(url)))

  lazy val title = feed.getTitle
  lazy val description = feed.getDescription
  lazy val authors = feed.getAuthors.asScala.map(_.getName).toList
  lazy val link = feed.getLink
  lazy val updatedAt = Temporal.date2OffsetDateTime(feed.getPublishedDate)

  lazy val entries = feed.getEntries.asScala.map { entry =>
    val title = entry.getTitle
    val author = entry.getAuthor
    val link = entry.getLink
    val content = entry.getDescription.getValue
    val date = Temporal.date2OffsetDateTime(entry.getPublishedDate)
    (date, title, author, link, content)
  }.toList

  /** Retrieves last entries given a date. */
  def since(datetime: OffsetDateTime): List[(OffsetDateTime, String, String, String)] =
    entries
      .filter(_._1.compareTo(datetime) > 0)
      .map(t => (t._1, t._2, t._3, t._4))

  /** Retrieves last entries given a date and filter only entries containg filter, terms */
  def sinceFiltered(datetime: OffsetDateTime, filter: String): List[(OffsetDateTime, String, String, String)] = {
    if (filter.isEmpty) since(datetime)
    else {
      val terms = filter.split(',').map(_.trim)
      entries
        .filter { case (date, _, _, _, content) =>
          date.compareTo(datetime) > 0 && terms.exists(content.contains)
        }
        .map(t => (t._1, t._2, t._3, t._4))
    }
  }

}
