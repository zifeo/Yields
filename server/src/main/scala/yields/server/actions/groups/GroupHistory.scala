package yields.server.actions.groups

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
 * Fetch each group node between two dates with time.
 * @param nid node id
 * @param lastTid last time identifier related to the given node
 * @param count number of node wanted
 */
case class GroupHistory(nid: NID, lastTid: TID, count: Int) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {
    if (nid > 0 && lastTid > 0 && count > 0) {
      val group = Group(nid)
      val content = group.getMessagesInRange(lastTid, count).toSeq
      GroupHistoryRes(content)
    } else {
      throw new ActionArgumentException("Bad arguments value")
    }
  }

}

/** [[GroupHistory]] result. */
case class GroupHistoryRes(nodes: Seq[FeedContent]) extends Result

