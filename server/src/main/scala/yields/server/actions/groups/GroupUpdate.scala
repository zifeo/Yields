package yields.server.actions.groups

import yields.server.Yields
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.{Action, Broadcast, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Update a group given given specific fields.
  * @param nid group id
  * @param name new name
  * @param pic new profile image
  * @param addUsers users to add
  * @param removeUsers users to remove
  * @param addNodes nodes to add
  * @param removeNodes nodes to remove
  *                    TODO: set picture
  */
case class GroupUpdate(nid: NID,
                       name: Option[String],
                       pic: Option[Blob],
                       addUsers: List[UID],
                       removeUsers: List[UID],
                       addNodes: List[NID],
                       removeNodes: List[NID]) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val group = Group(nid)
    val sender = metadata.client

    if (!group.users.contains(sender))
      throw new UnauthorizedActionException(s"$sender does not belong to $nid")

    for (newName <- name) {
      group.name = newName
    }

    for (newPic <- pic) {
      group.pic(newPic, sender)
    }

    if (addUsers.nonEmpty) {
      group.addUser(addUsers)
    }
    addUsers.foreach(User(_).addNode(group.nid))

    if (removeUsers.nonEmpty) {
      group.removeUser(removeUsers)
    }
    removeUsers.foreach(User(_).removeNode(group.nid))

    if (addNodes.nonEmpty) {
      group.addNode(addNodes)
    }

    if (removeNodes.nonEmpty) {
      group.removeNode(removeNodes)
    }

    Yields.broadcast(group.users.filter(_ != sender)) {
      GroupUpdateBrd(group.nid, group.name, group.pic, group.users, group.nodes)
    }

    GroupUpdateRes()

  }

}

/** [[GroupUpdate]] result. */
case class GroupUpdateRes() extends Result

/**
  * [[GroupUpdate]] broadcast.
  * @param nid group id
  * @param name new group name
  * @param pic new group pic
  * @param users group users
  * @param nodes group nodes
  */
case class GroupUpdateBrd(nid: NID, name: String, pic: Blob, users: Seq[UID], nodes: Seq[NID]) extends Broadcast