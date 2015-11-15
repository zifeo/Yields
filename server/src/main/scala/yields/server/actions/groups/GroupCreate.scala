package yields.server.actions.groups

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Creation of a named group including some nodes
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
    if (!name.isEmpty) {
      val group = Group.createGroup(name)
      nodes.foreach(group.addNode)
      GroupCreateRes(group.nid)
    } else {
      throw new ActionArgumentException(s"Empty name : ${this.getClass.getSimpleName}")
    }
  }

}

/**
  * [[GroupCreate]] result.
  * @param nid the nid of newly created group
  */
case class GroupCreateRes(nid: NID) extends Result
