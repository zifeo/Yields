package yields.server.actions.groups

import java.time.OffsetDateTime

import yields.server.actions.{Result, Broadcast}
import yields.server.actions.nodes.NodeMessage
import yields.server.dbi.models.{UID, Blob, NID}

/**
  * Message related to a group.
  * @param nid group id
  * @param text message text
  * @param contentType message content type
  * @param content message content
  */
case class GroupMessage(nid: NID, text: Option[String], contentType: Option[String], content: Option[Blob])
  extends NodeMessage(nid, text, contentType, content) {

  /** Format the result. */
  override def result(datetime: OffsetDateTime): Result =
    GroupMessageRes(nid, datetime)

  /** Format the broadcast. */
  override def broadcast(datetime: OffsetDateTime, uid: UID): Broadcast =
    GroupMessageBrd(nid, datetime, uid, text, contentType, content)

}

/**
  * [[GroupMessage]] result.
  * @param nid group id
  * @param datetime message recorded datetime
  */
case class GroupMessageRes(nid: NID, datetime: OffsetDateTime) extends Result

/**
  * [[GroupMessage]] broadcast.
  * @param nid group id
  * @param datetime message recorded datetime
  * @param sender message sender
  * @param text message text if any
  * @param contentType message content type if any
  * @param content message content if any
  */
case class GroupMessageBrd(nid: NID,
                           datetime: OffsetDateTime,
                           sender: UID,
                           text: Option[String],
                           contentType: Option[String],
                           content: Option[Blob]) extends Broadcast
