package yields.server.actions.media

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.{Result, Broadcast}
import yields.server.actions.nodes.{NodeMessageBrd, NodeMessage}
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

  /** Get node instance. */
  override def instance(nid: NID): Node = Media(nid)

  /** Format the result. */
  override def result(datetime: OffsetDateTime, contentNid: Option[NID]): Result =
    MediaMessageRes(nid, datetime)

  /** Sent the broadcast. */
  override def broadcast(users: List[UID], datetime: OffsetDateTime, uid: UID, contentNid: Option[NID]): Unit = {
    val bcast = NodeMessageBrd(nid, datetime, uid, text, contentType, content, contentNid)
    Yields.broadcast(users)(bcast)
  }

}

/**
  * [[MediaMessage]] result.
  * @param nid media id
  * @param datetime message recorded datetime
  */
case class MediaMessageRes(nid: NID, datetime: OffsetDateTime) extends Result
