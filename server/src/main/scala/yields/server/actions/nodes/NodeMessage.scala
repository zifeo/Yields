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
      var media: Option[Media] = None
      if (contentType.isDefined && content.isDefined) {
        media = Some(Media.createMedia(contentType.get, content.get))
      }

      if (media.isDefined) {
        group.addMessage((Temporal.current, metadata.sender, Some(media.get.nid), text.getOrElse("")))
      } else {
        group.addMessage((Temporal.current, metadata.sender, None, text.getOrElse("")))
      }
    }
    NodeMessageRes(true)
  }

}

/** [[NodeMessage]] result. */
case class NodeMessageRes(b: Boolean) extends Result
