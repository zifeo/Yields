package yields.server.dbi

import java.time.OffsetDateTime

import com.redis.serialization.{Format, Parse}
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

  /** */
  type FeedContent = (OffsetDateTime, UID, Option[NID], String)

  import Parse.Implicits._

  /** [[OffsetDateTime]] Redis parser. */
  implicit val parseOffsetDateTime = Parse[OffsetDateTime](byteArray => OffsetDateTime.parse(byteArray))

  /** [[FeedContent]] Redis parser. */
  implicit val parseFeedContent = Parse[FeedContent] { byteArray =>
    val (datetime, uidNidText) = byteArray.span(_ != ',')
    val (uid, nidText) = uidNidText.drop(1).span(_ != ',')
    val (nidOption, text) = nidText.drop(1).span(_ != ',')
    val nid: Option[NID] = if (nidOption.nonEmpty) Some(nidOption) else None
    (datetime,uid,nid,text.drop(1))
  }

  /** [[FeedContent]] Redis format. */
  implicit val formatFeedContent = Format {
    case (datetime, uid, Some(nid), text) => s"$datetime,$uid,$nid,$text"
    case (datetime, uid, None, text) => s"$datetime,$uid,,$text"
  }

}
