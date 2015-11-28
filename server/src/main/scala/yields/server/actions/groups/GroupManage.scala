package yields.server.actions.groups

import yields.server.actions.{Result, Action}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

case class GroupManage(nid: NID, addedUser: Option[UID], removedUser: Option[UID],
                       addedNodes: Option[List[NID]], removedNodes: Option[List[NID]]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val group = Group(nid)
    if (addedUser.isDefined) {
      group.addUser(addedUser.get)
    }

    if (removedUser.isDefined) {
      group.removeUser(removedUser.get)
    }

    if (addedNodes.isDefined) {
      group.addNode(addedNodes.get)
    }

    if (removedNodes.isDefined) {
      group.removeNode(removedNodes.get)
    }
    GroupManageRes()
  }
}

case class GroupManageRes() extends Result