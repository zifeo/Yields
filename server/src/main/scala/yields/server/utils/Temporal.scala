package yields.server.utils

import java.time.{OffsetDateTime, ZoneId}
import java.util.Date

/** Regroups all temporal utilities. */
object Temporal {

  /** Converts an old java date to new java 8 offset datetime. */
  def date2OffsetDateTime(date: Date): OffsetDateTime =
    OffsetDateTime.ofInstant(date.toInstant, ZoneId.systemDefault())

  /** Converts a new java 8 offset datetime to an old java date. */
  def offsetDateTime2Date(datetime: OffsetDateTime): Date =
    Date.from(datetime.toInstant)

  /** Returns current date and time. */
  def current: OffsetDateTime = date2OffsetDateTime(new Date())

  /** Returns a date not yet set. */
  def notYet: OffsetDateTime = OffsetDateTime.MIN

}
