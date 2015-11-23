package yields.server.actions.nodes

import java.time.OffsetDateTime

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Message related to a particular group.
  * @param nid group id
  * @param content message content
  */
case class NodeMessage(nid: NID, text: Option[String], contentType: Option[String], content: Option[Blob]) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (nid > 0) {
      val group = Group(nid)
      val datetime = Temporal.now

      val media = for {
        cntType <- contentType
        cnt <- content
      } yield Media.createMedia(cntType, cnt, metadata.client)

      media match {
        case Some(x) => group.addMessage((datetime, metadata.client, Some(x.nid), text.getOrElse("")))
        case None => group.addMessage((datetime, metadata.client, None, text.getOrElse("")))
      }

      NodeMessageRes(datetime)
    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"Bad nid value in : $errorMessage")
    }
  }

}

/** [[NodeMessage]] result. */
case class NodeMessageRes(datetime: OffsetDateTime) extends Result
