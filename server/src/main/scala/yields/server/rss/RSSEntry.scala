package yields.server.rss

import java.time.OffsetDateTime

/**
  * A RSS entry
  * @param title entry title
  * @param author entry author
  * @param link entry link
  * @param datetime entry datetime
  * @param content entry content
  */
case class RSSEntry(title: String, author: String, link: String, datetime: OffsetDateTime, content: String)
