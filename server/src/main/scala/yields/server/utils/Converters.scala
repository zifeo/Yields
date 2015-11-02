package yields.server.utils

import java.time.{ZoneId, OffsetDateTime}
import java.util.Date

object Converters {

  /** Converts an old java date to new java 8 offset datetime. */
  def date2OffsetDateTime(date: Date): OffsetDateTime =
    OffsetDateTime.ofInstant(date.toInstant, ZoneId.systemDefault())

  /** Converts a new java 8 offset datetime to an old java date. */
  def offsetDateTime2Date(datetime: OffsetDateTime): Date =
    Date.from(datetime.toInstant)


}
