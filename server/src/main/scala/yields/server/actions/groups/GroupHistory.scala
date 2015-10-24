package yields.server.actions.groups

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{GID, NID, Node, UID}

/**
 * Fetch each group node between two dates with time.
 * @param gid group id
 * @param lastNid last node id
 * @param count number of node wanted
 */
case class GroupHistory(gid: GID, lastNid: NID, count: Int) extends Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  override def run(sender: UID): Result = {
    GroupHistoryRes(Seq.empty)
  }

}

/** [[GroupHistory]] result. */
case class GroupHistoryRes(nodes: Seq[Node]) extends Result

