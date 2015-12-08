package yields.server.actions.groups

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.Result
import yields.server.actions.nodes.{NodeMessage, NodeMessageBrd}
import yields.server.dbi.models._

/**
  * Message related to a group.
  * @param nid group id
  * @param text message text
  * @param contentType message content type
  * @param content message content
  */
case class GroupMessage(nid: NID, text: Option[String], contentType: Option[String], content: Option[Blob])
  extends NodeMessage(nid, text, contentType, content) {

  /** Get node instance. */
  override def instance(nid: NID): Node =
    Group(nid)

  /** Format the result. */
  override def result(datetime: OffsetDateTime, contentNid: Option[NID]): Result =
    GroupMessageRes(nid, datetime, contentNid)

  /** Sent the broadcast. */
  override def broadcast(users: List[UID], datetime: OffsetDateTime, uid: UID, contentNid: Option[NID]): Unit = {
    val bcast = NodeMessageBrd(nid, datetime, uid, text, contentType, content, contentNid)
    Yields.broadcast(users)(bcast)
  }

}

/**
  * [[GroupMessage]] result.
  * @param nid group id
  * @param datetime message recorded datetime
  */
case class GroupMessageRes(nid: NID, datetime: OffsetDateTime, contentNid: Option[NID]) extends Result
