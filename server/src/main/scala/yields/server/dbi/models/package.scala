package yields.server.dbi

import java.time.OffsetDateTime

import com.redis.serialization.Parse

/**
 * Provisory models types.
 */
package object models {

  private type ID = Long

  /** Represents an user id. */
  type UID = ID

  /** Represents a group id. */
  type GID = ID

  /** Represents a node id. */
  type NID = ID

  /** Represent an item of group content */
  type TID = ID

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = String

  /** Represents a node. */
  // type Node = Int

  /** */
  type FeedContent = (OffsetDateTime, UID, Option[NID], String)

  import Parse.Implicits._

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
