package yields.server.actions.groups

import yields.server.Yields
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.{Action, Broadcast, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Creation of a named group including some nodes
  * @param name group name
  * @param users users to put in the group
  * @param nodes grouped nodes
  */
case class GroupCreate(name: String, users: List[UID], nodes: List[NID]) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val user = User(metadata.client)
    val entourage = user.entourage
    val sender = user.uid

    if (! users.filter(_ != sender).forall(entourage.contains))
      throw new UnauthorizedActionException(s"not all users are $sender's entourage: $users")

    // TODO: check public node

    val group = Group.create(name, sender)

    if (users.nonEmpty) {
      group.addUser(users)
    }

    if (nodes.nonEmpty) {
      group.addNode(nodes)
    }

    user.addGroup(group.nid)

    Yields.broadcast(group.users.filter(_ != sender)) {
      GroupCreateBrd(group.nid, name, users, nodes)
    }

    GroupCreateRes(group.nid)

  }

}

/**
  * [[GroupCreate]] result.
  * @param nid the nid of newly created group
  */
case class GroupCreateRes(nid: NID) extends Result

/**
  * [[GroupCreate]] broadcast.
  * @param nid new group id
  * @param name new group name
  * @param users new group users
  * @param nodes new group nodes
  */
case class GroupCreateBrd(nid: NID, name: String, users: Seq[UID], nodes: Seq[NID]) extends Broadcast