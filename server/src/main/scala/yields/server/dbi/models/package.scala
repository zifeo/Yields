package yields.server.dbi

import java.time.OffsetDateTime

import com.redis.serialization.{Format, Parse}
import com.redis.serialization.Parse.Implicits._

/**
  * Short models types and some formats.
  */
package object models {

  /** Represents an user identifier. */
  type UID = Long

  /** Represents a node identifier. */
  type NID = Long

  /** Represent a tag identifier */
  type TID = Long

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = String

  /** Feed content coming from client */
  type IncomingFeedContent = (OffsetDateTime, UID, Option[NID], String)

  /** Feed content answering the client */
  type ResponseFeedContent = (OffsetDateTime, UID, Option[Blob], String)

  import Parse.Implicits._

  /** [[OffsetDateTime]] Redis parser. */
  implicit val parseOffsetDateTime = Parse[OffsetDateTime](byteArray => OffsetDateTime.parse(byteArray))

  /** [[IncomingFeedContent]] Redis parser. */
  implicit val parseFeedContent = Parse[IncomingFeedContent] { byteArray =>
    val (datetime, uidNidText) = byteArray.span(_ != ',')
    val (uid, nidText) = uidNidText.drop(1).span(_ != ',')
    val (nidOption, text) = nidText.drop(1).span(_ != ',')
    val nid: Option[NID] = if (nidOption.nonEmpty) Some(nidOption) else None
    (datetime, uid, nid, text.drop(1))
  }

  /** [[IncomingFeedContent]] Redis format. */
  implicit val formatFeedContent = Format {
    case (datetime, uid, Some(nid), text) => s"$datetime,$uid,$nid,$text"
    case (datetime, uid, None, text) => s"$datetime,$uid,,$text"
  }

}
