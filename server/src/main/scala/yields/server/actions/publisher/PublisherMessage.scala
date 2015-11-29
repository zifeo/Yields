package yields.server.actions.publisher

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.{Broadcast, Result, Action}
import yields.server.dbi.models.Media.create
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Send a message to a publisher
  * @param nid nid of publisher
  * @param text text to send
  * @param contentType content type of a potential media
  * @param content content of the media
  */
case class PublisherMessage(nid: NID, text: Option[String], contentType: Option[String], content: Option[Blob])
  extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val publisher = Publisher(nid)
    val datetime = Temporal.now
    if (!publisher.users.contains(metadata.client))
      throw new UnauthorizedActionException(s"evil user ${metadata.client} can't publish in publisher $nid")

    val media = for {
      cntType <- contentType
      cnt <- content
    } yield create(cntType, cnt, metadata.client)

    publisher.addMessage((datetime, metadata.client, media.map(_.nid), text.getOrElse("")))

    Yields.broadcast(publisher.users.filter(_ != metadata.client)) {
      PublisherMessageBrd(nid, datetime, metadata.client, text, contentType, content)
    }

    PublisherMessageRes(nid, datetime)
  }

}

/**
  * [[PublisherMessage]] Result
  * @param nid nid of publisher
  * @param datetime time when action was executed
  */
case class PublisherMessageRes(nid: NID, datetime: OffsetDateTime) extends Result

/**
  * [[PublisherMessage]] broadcast
  * @param nid nid of publisher
  * @param datetime time when action was executed
  * @param sender message sender
  * @param text text sent
  * @param contentType content type if content is specified
  * @param content content if a media was specified
  */
case class PublisherMessageBrd(nid: NID, datetime: OffsetDateTime, sender: UID, text: Option[String], contentType: Option[String], content: Option[Blob]) extends Broadcast