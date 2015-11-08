package yields.server.actions.groups

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.NID
import yields.server.mpi.Metadata

/**
 * Creation of a named group including some nodes.
 * @param name group name
 * @param nodes grouped nodes
 */
case class GroupCreate(name: String, nodes: Seq[NID]) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {

    GroupCreateRes(1)
  }

}

/** [[GroupCreate]] result. */
case class GroupCreateRes(nid: NID) extends Result
