package yields.server.actions.groups

import java.time.OffsetDateTime

import yields.server.actions.{Action, Result}
import yields.server.models.{GID, Node, UID}

/**
 * Fetch each group node between two dates with time.
 * @param gid group id
 * @param from start date
 * @param to end date
 */
case class GroupHistory(gid: GID, from: OffsetDateTime, to: OffsetDateTime) extends Action {

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

