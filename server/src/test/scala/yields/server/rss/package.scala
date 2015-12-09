package yields.server

import java.time.OffsetDateTime

import com.rometools.rome.feed.synd.{SyndContentImpl, SyndEntryImpl, SyndEntry, SyndFeedImpl}
import yields.server.utils.Temporal

package object rss {

  /** Fake a feed. */
  def createFeed(title: String, desc: String, author: String, link: String, datetime: OffsetDateTime): SyndFeedImpl = {
    val feed = new SyndFeedImpl()
    feed.setFeedType("rss_2.0")
    feed.setTitle(title)
    feed.setDescription(desc)
    feed.setAuthor(author)
    feed.setLink(link)
    feed.setPublishedDate(Temporal.offsetDateTime2Date(datetime))
    feed
  }

  /** Fake an entry. */
  def createEntry(title: String, desc: String, author: String, link: String, datetime: OffsetDateTime): SyndEntry = {
    val entry = new SyndEntryImpl
    entry.setTitle(title)
    val description = new SyndContentImpl()
    description.setType("text/plain")
    description.setValue(desc)
    entry.setDescription(description)
    entry.setAuthor(author)
    entry.setLink(link)
    entry.setPublishedDate(Temporal.offsetDateTime2Date(datetime))
    entry
  }

}
