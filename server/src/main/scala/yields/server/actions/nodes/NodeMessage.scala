package yields.server.actions.nodes

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

      val media = for {
        cntType <- contentType
        cnt <- content
      } yield Media.createMedia(cntType, cnt, metadata.sender)

      media match {
        case Some(x) => group.addMessage((Temporal.current, metadata.sender, Some(x.nid), text.getOrElse("")))
        case None => group.addMessage((Temporal.current, metadata.sender, None, text.getOrElse("")))
      }

    }
    NodeMessageRes(nid)
  }

}

/** [[NodeMessage]] result. */
case class NodeMessageRes(nid: NID) extends Result
