package yields.server.actions.publisher

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.Result
import yields.server.actions.nodes.{NodeMessage, NodeMessageBrd}
import yields.server.dbi.models._

/**
  * Send a message to a publisher.
  * @param nid nid of publisher
  * @param text text to send
  * @param contentType content type of a potential media
  * @param content content of the media
  */
case class PublisherMessage(nid: NID, text: Option[String], contentType: Option[String], content: Option[Blob])
  extends NodeMessage(nid, text, contentType, content) {

  /** Get node instance. */
  override def instance(nid: NID): Node =
    Publisher(nid)

  /** Format the result. */
  override def result(datetime: OffsetDateTime, contentNid: Option[NID]): Result =
    PublisherMessageRes(nid, datetime, contentNid)

  /** Sent the broadcast. */
  override def broadcast(users: List[UID], datetime: OffsetDateTime, uid: UID, contentNid: Option[NID]): Unit = {
    val bcast = NodeMessageBrd(nid, datetime, uid, text, contentType, content, contentNid)
    Yields.broadcast(users)(bcast)

    Publisher(nid).receivers.map(Node(_)).foreach { node =>
      Yields.broadcast(node.users)(bcast.copy(nid = node.nid, sender = nid))
    }
  }

}

/**
  * [[PublisherMessage]] result.
  * @param nid nid of publisher
  * @param datetime time when action was executed
  */
case class PublisherMessageRes(nid: NID, datetime: OffsetDateTime, contentNid: Option[NID]) extends Result
