package yields.server.actions.groups

import yields.server.dbi.models.{GID, UID, NID}
import yields.server.actions.{Result, Action}

/**
 * Creation of a named group including some nodes.
 * @param name group name
 * @param nodes grouped nodes
 */
case class GroupCreate(name: String, nodes: Seq[NID]) extends Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  override def run(sender: UID): Result = {
    GroupCreateRes(1)
  }

}

/** [[GroupCreate]] result. */
case class GroupCreateRes(gid: GID) extends Result
