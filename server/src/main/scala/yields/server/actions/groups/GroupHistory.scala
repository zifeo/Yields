package yields.server.actions.groups

import yields.server.models.{Node, UID, DateTime, GID}
import yields.server.actions.{Result, Action}

/**
 * Fetch each group node between two dates with time.
 * @param gid group id
 * @param from start date
 * @param to end date
 */
case class GroupHistory(gid: GID, from: DateTime, to: DateTime) extends Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  def run(sender: UID): Result = {
    GroupHistoryRes(Seq.empty)
  }

}

/** [[GroupHistory]] result. */
case class GroupHistoryRes(nodes: Seq[Node]) extends Result

