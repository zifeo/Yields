package yields.server.dbi

import java.time.OffsetDateTime

import com.redis.serialization.{Format, Parse}

/**
  * Short models types and some formats.
  */
package object models {

  private type Identity = Long

  /** Represents an user identifier. */
  type UID = Identity

  /** Represents a node identifier. */
  type NID = Identity

  /** Represent a tag identifier */
  type TID = Long

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = Array[Byte]

  /** Represents a feed content (or so called message). */
  type FeedContent = (OffsetDateTime, UID, Option[NID], String)

  import Parse.Implicits._

  /** Redis format for [[FeedContent]], [[OffsetDateTime]].  */
  implicit val formatFeedContent = Format {
    case (datetime, uid, Some(nid), text) => s"$datetime,$uid,$nid,$text"
    case (datetime, uid, None, text) => s"$datetime,$uid,,$text"
    case datetime: OffsetDateTime => datetime.toString
  }

  /** [[OffsetDateTime]] Redis parser. */
  implicit val parseOffsetDateTime = Parse[OffsetDateTime](byteArray => OffsetDateTime.parse(byteArray))

  /** [[FeedContent]] Redis parser. */
  implicit val parseFeedContent = Parse[FeedContent] { byteArray =>
    byteArray.split(",", 4) match {
      case Array(datetime, uid, "", text) =>
        (OffsetDateTime.parse(datetime), uid.toLong, None, text)

      case Array(datetime, uid, nid, text) =>
        (OffsetDateTime.parse(datetime), uid.toLong, Some(nid.toLong), text)
    }
  }

}
