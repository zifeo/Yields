package yields.server.actions.publisher

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.groups.GroupMessageRes
import yields.server.actions.nodes.NodeMessage
import yields.server.actions.{Broadcast, Result, Action}
import yields.server.dbi.models.Media.create
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

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

  /** Format the broadcast. */
  override def broadcast(datetime: OffsetDateTime, uid: UID, contentNid: Option[NID]): Broadcast =
    PublisherMessageBrd(nid, datetime, uid, text, contentType, content, contentNid)

}

/**
  * [[PublisherMessage]] result.
  * @param nid nid of publisher
  * @param datetime time when action was executed
  */
case class PublisherMessageRes(nid: NID, datetime: OffsetDateTime, contentNid: Option[NID]) extends Result

/**
  * [[PublisherMessage]] broadcast.
  * @param nid nid of publisher
  * @param datetime time when action was executed
  * @param sender message sender
  * @param text text sent
  * @param contentType content type if content is specified
  * @param content content if a media was specified
  */
case class PublisherMessageBrd(nid: NID,
                               datetime: OffsetDateTime,
                               sender: UID,
                               text: Option[String],
                               contentType: Option[String],
                               content: Option[Blob],
                               contentNid: Option[NID]) extends Broadcast