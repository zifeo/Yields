package yields.server.actions.media

import java.time.OffsetDateTime

import yields.server.actions.{Result, Broadcast}
import yields.server.actions.nodes.NodeMessage
import yields.server.dbi.models._

/**
  * Add message (comment) on a media
  * @param nid group id
  * @param text message text
  * @param contentType message content type
  * @param content message content
  */
case class MediaMessage(nid: NID, text: Option[String], contentType: Option[String], content: Option[Blob])
  extends NodeMessage(nid, text, contentType, content) {
  /** Format the result. */
  override def result(datetime: OffsetDateTime, contentNid: Option[NID]): Result =
    MediaMessageRes(nid, datetime)

  /** Format the broadcast. */
  override def broadcast(datetime: OffsetDateTime, uid: UID, contentNid: Option[NID]): Broadcast =
    MediaMessageBrd(nid, datetime, uid, text, contentType, content)

  /** Get node instance. */
  override def instance(nid: NID): Node = Media(nid)
}

/**
  * [[MediaMessage]] result.
  * @param nid media id
  * @param datetime message recorded datetime
  */
case class MediaMessageRes(nid: NID, datetime: OffsetDateTime) extends Result

/**
  * [[MediaMessage]] broadcast.
  * @param nid media id
  * @param datetime message recorded datetime
  * @param sender message sender
  * @param text message text if any
  * @param contentType message content type if any
  * @param content message content if any
  */
case class MediaMessageBrd(nid: NID,
                           datetime: OffsetDateTime,
                           sender: UID,
                           text: Option[String],
                           contentType: Option[String],
                           content: Option[Blob]) extends Broadcast