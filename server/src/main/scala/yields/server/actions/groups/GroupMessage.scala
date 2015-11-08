package yields.server.actions.groups

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
case class GroupMessage(nid: NID, content: String) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {
    if (nid > 0) {
      if (!content.isEmpty) {
        val group = Group(nid)
        val c = (Temporal.current, metadata.sender, None, content)
        group.addMessage(c)
        GroupMessageRes()
      } else {
        throw new ActionArgumentException("Empty content")
      }
    } else {
      throw new ActionArgumentException("Bad nid value")
    }
  }

}

/** [[GroupMessage]] result. */
case class GroupMessageRes() extends Result
