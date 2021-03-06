package yields.server.actions.nodes

import java.time.OffsetDateTime

import yields.server.actions.exceptions.{ActionArgumentException, UnauthorizedActionException}
import yields.server.actions.{Action, Broadcast, Result}
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

    val node = instance(nid)
    val sender = metadata.client

    if (!node.users.contains(sender))
      throw new UnauthorizedActionException(s"$sender does not belong to $nid")

    if (contentType.isDefined != content.isDefined)
      throw new ActionArgumentException(s"content must have a corresponding content type")

    if (text.isEmpty && content.isEmpty)
      throw new ActionArgumentException(s"at least text or content must be defined")

    authorize(metadata)

    val media = for {
      cntType <- contentType
      cnt <- content
    } yield Media.create(cntType, cnt, sender)

    media.map(_.addUser(node.users))

    val datetime = Temporal.now
    val mediaNid = media.map(_.nid)

    node.addMessage((datetime, metadata.client, mediaNid, text.getOrElse("")))
    broadcast(node.users.filter(_ != sender), datetime, sender, mediaNid)

    result(datetime, mediaNid)

  }

  /** Get node instance. */
  def instance(nid: NID): Node

  /**
    * Allow to enlarge restriction using the metadata and throwing an [[UnauthorizedActionException]].
    * @param metadata requester
    * @throws UnauthorizedActionException on additional restrictions
    */
  def authorize(metadata: Metadata): Unit = ()

  /** Format the result. */
  def result(datetime: OffsetDateTime, contentNid: Option[NID]): Result

  /** Sent the broadcast. */
  def broadcast(users: List[UID], datetime: OffsetDateTime, uid: UID, contentNid: Option[NID]): Unit = ()

}

/**
  * [[NodeMessage]] broadcast.
  * @param nid node nid
  * @param datetime time when message was registered
  * @param sender message sender
  * @param text text sent
  * @param contentType content type if content is specified
  * @param content content if a media was specified
  * @param contentNid content id if a media was specified
  */
case class NodeMessageBrd(nid: NID,
                          datetime: OffsetDateTime,
                          sender: UID,
                          text: Option[String],
                          contentType: Option[String],
                          content: Option[Blob],
                          contentNid: Option[NID]) extends Broadcast