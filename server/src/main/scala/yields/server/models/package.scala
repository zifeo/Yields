package yields.server

import java.text.SimpleDateFormat
import java.time.OffsetDateTime

import yields.server.utils.Helpers

/**
 * Provisory models types.
 */
package object models {

  private type ID = Long
  private val databaseDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  /** Represents an user id. */
  type UID = ID

  /** Represents a group id. */
  type GID = ID

  /** Represents a node id. */
  type NID = ID

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = String

  /** Represents a node. */
  type Node = Int

  /** Returns current database formatted date and time. */
  def databaseDateTime: String = databaseDateTime(Helpers.currentDatetime)

  /** Returns given database formatted date and time. */
  def databaseDateTime(datetime: OffsetDateTime): String =  databaseDateTimeFormat.format(datetime)

}
