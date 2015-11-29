package yields.server.actions.groups

import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Returns all group information.
  * @param nid group id
  */
case class GroupInfo(nid: NID) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val group = Group(nid)
    val sender = metadata.client

    if (! group.users.contains(sender))
      throw new UnauthorizedActionException(s"$sender does not belong to $nid")

    group.hydrate()
    GroupInfoRes(group.nid, group.name, Array.empty, group.users, group.nodes)

  }

}

/**
  * [[GroupInfo]] result.
  * @param nid group id
  * @param name group name
  * @param pic group pic
  * @param users group users
  * @param nodes group nodes
  */
case class GroupInfoRes(nid: NID, name: String, pic: Blob, users: Seq[UID], nodes: Seq[NID]) extends Result