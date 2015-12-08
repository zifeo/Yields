package yields.server.rss

import java.net.URL

import com.rometools.rome.io.{XmlReader, SyndFeedInput}

import scala.collection.JavaConverters._

object Parser extends App {

  val feedUrl = new URL("http://stackoverflow.com/feeds")
  val feed = new SyndFeedInput().build(new XmlReader(feedUrl))

  println(feed.getTitle)
  println(feed.getDescription)
  println(feed.getPublishedDate)
  println(feed.getAuthors)
  println(feed.getLink)
  println()

  feed.getEntries.asScala.foreach { entry =>

    println(entry.getTitle)
    println(entry.getPublishedDate)
    println(entry.getAuthor)
    println(entry.getLink)

  }

}
