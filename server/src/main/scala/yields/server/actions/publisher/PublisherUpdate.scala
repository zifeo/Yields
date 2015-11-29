package yields.server.actions.publisher

import yields.server.actions.exceptions.{UnauthorizedActionException}
import yields.server.actions.{Result, Action}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Update publisher infos
  * @param nid publisher to update
  * @param name new name
  * @param pic new "profile" picture
  * @param addUsers users to add
  * @param removeUsers users to remove
  * @param addNodes nodes to add
  * @param removeNodes nodes to remove
  *
  */
case class PublisherUpdate(nid: NID, name: Option[String], pic: Option[Blob], addUsers: Seq[UID],
                           removeUsers: Seq[UID], addNodes: Seq[NID], removeNodes: Seq[NID]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val sender = User(metadata.client)

    val publisher = Publisher(nid)

    if (!publisher.users.contains(metadata.client))
      throw new UnauthorizedActionException("the publisher can only be updated by a user who can publish")

    for (newName <- name) {
      publisher.name = newName
    }

    for (newPic <- pic) {
      //group.pic = newPic
    }

    if (addUsers.nonEmpty) {
      publisher.addUser(addUsers.toList)
    }

    if (removeUsers.nonEmpty) {
      publisher.removeUser(removeUsers.toList)
    }

    if (addNodes.nonEmpty) {
      publisher.addNode(addNodes.toList)
    }

    if (removeNodes.nonEmpty) {
      publisher.removeNode(removeNodes.toList)
    }
    PublisherUpdateRes()
  }
}

case class PublisherUpdateRes() extends Result
