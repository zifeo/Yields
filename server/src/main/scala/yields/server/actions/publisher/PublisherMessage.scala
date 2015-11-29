package yields.server.actions.publisher

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.Media.createMedia
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
      throw new UnauthorizedActionException("only registered user can publish in a publisher")

    val media = for {
      cntType <- contentType
      cnt <- content
    } yield createMedia(cntType, cnt, metadata.client)

    media match {
      case Some(x) => publisher.addMessage((datetime, metadata.client, Some(x.nid), text.getOrElse("")))
      case None => publisher.addMessage((datetime, metadata.client, None, text.getOrElse("")))
    }

    PublisherMessageRes(nid, datetime)
  }

}

case class PublisherMessageRes(nid: NID, datetime: OffsetDateTime) extends Result