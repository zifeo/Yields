package yields.server.actions.nodes

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.{Broadcast, Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Message related to a node.
  * @param nid group id
  * @param text message text
  * @param contentType message content type
  * @param content message content
  */
abstract class NodeMessage(nid: NID, text: Option[String], contentType: Option[String], content: Option[Blob])
  extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override final def run(metadata: Metadata): Result = {

    val node = Node(nid)
    val sender = metadata.client

    if (! node.users.contains(sender))
      throw new UnauthorizedActionException(s"$sender does not belong to $nid")

    if (contentType.isDefined != content.isDefined)
      throw new ActionArgumentException(s"content must a corresponding content type")

    if (text.isEmpty && content.isEmpty)
      throw new ActionArgumentException(s"at least text or content must be defined")

    authorize(metadata)

    val datetime = Temporal.now
    val media = for {
      cntType <- contentType
      cnt <- content
    } yield Media.createMedia(cntType, cnt, sender)

    assert(node.addMessage((datetime, metadata.client, media.map(_.nid), text.getOrElse(""))))

    Yields.broadcast(node.users) {
      broadcast(datetime, sender)
    }

    result(datetime)
  }

  /**
    * Allow to enlarge restriction using the metadata and throwing an [[UnauthorizedActionException]].
    * @param metadata requester
    * @throws UnauthorizedActionException on additional restrictions
    */
  def authorize(metadata: Metadata): Unit = ()

  /** Format the result. */
  def result(datetime: OffsetDateTime): Result

  /** Format the broadcast. */
  def broadcast(datetime: OffsetDateTime, uid: UID): Broadcast

}

