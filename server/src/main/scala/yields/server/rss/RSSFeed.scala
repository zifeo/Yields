package yields.server.rss

import java.net.URL
import java.time.OffsetDateTime

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.{SyndFeedInput, XmlReader}
import yields.server.utils.Temporal

import scala.collection.JavaConverters._

/**
  * Takes care of parsing a valid RSS feed and offer information retrieval manner.
  * RSS example: http://stackoverflow.com/feeds
  *
  * @param feed RSS feed
  */
final class RSSFeed(private val feed: SyndFeed) {

  /** Create a RSS from a string representing an url. */
  def this(url: String) {
    this(new SyndFeedInput().build(new XmlReader(new URL(url))))
  }

  require(feed != null)

  lazy val title = notNull(feed.getTitle)
  lazy val description = notNull(feed.getDescription)
  lazy val author = notNull(feed.getAuthor)
  lazy val link = notNull(feed.getLink)
  lazy val updatedAt = Temporal.date2OffsetDateTime(feed.getPublishedDate)

  lazy val entries = feed.getEntries.asScala.map { entry =>
    val title = notNull(entry.getTitle)
    val author = notNull(entry.getAuthor)
    val link = notNull(entry.getLink)
    val date = Temporal.date2OffsetDateTime(entry.getPublishedDate)
    val content = notNull(entry.getDescription.getValue)
    RSSEntry(title, author, link, date, content)
  }.toList

  /** Retrieves last entries given a date. */
  def since(datetime: OffsetDateTime): List[RSSEntry] =
    entries.filter(_.datetime.compareTo(datetime) > 0)

  /** Retrieves last entries given a date and filter only entries containg filter, terms */
  def sinceFiltered(datetime: OffsetDateTime, filter: String): List[RSSEntry] = {
    val selected = since(datetime)
    val terms = filter.split(',').map(_.trim)
    println(terms.toList)
    if (terms.isEmpty) selected
    else
      selected.filter { entry =>
        entry.datetime.compareTo(datetime) > 0 && terms.exists(entry.content.contains)
      }
  }

  /** Returns given string or empty one if null. */
  private def notNull(str: String): String =
    if (str == null) ""
    else str

}
