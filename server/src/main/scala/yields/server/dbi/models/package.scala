package yields.server.dbi

import java.time.OffsetDateTime

import com.redis.serialization.Parse
import com.redis.serialization.Parse.Implicits._

/**
 * Short models types and some formats.
 */
package object models {

  private type ID = Long

  /** Represents an user identifier. */
  type UID = Long

  /** Represents a node identifier. */
  type NID = Long

  /** Represent a time identifier (used for indexing an item in a group content). */
  type TID = ID

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = String

  /** Represents a feed entry. */
  type FeedContent = (OffsetDateTime, UID, NID, String)

  /** [[OffsetDateTime]] Redis format. */
  implicit val parseOffsetDateTime = Parse[OffsetDateTime](byteArray => OffsetDateTime.parse(byteArray))

  /** [[FeedContent]] Redis format. */
  implicit val parseTuple = Parse[FeedContent] { byteArray =>
    val (datetime, uidNidText) = byteArray.span(_ == ',')
    val (uid, nidText) = uidNidText.span(_ == ',')
    val (nid, text) = nidText.span(_ == ',')
    (datetime, uid, nid, text)
  }

}
