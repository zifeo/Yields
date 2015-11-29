package yields.server.actions.publisher

import yields.server.Yields
import yields.server.actions.exceptions.{UnauthorizedActionException}
import yields.server.actions.{Broadcast, Result, Action}
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
case class PublisherUpdate(nid: NID, name: Option[String], pic: Option[Blob], addUsers: List[UID],
                           removeUsers: List[UID], addNodes: List[NID], removeNodes: List[NID]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val sender = User(metadata.client)

    val publisher = Publisher(nid)

    if (!publisher.users.contains(metadata.client))
      throw new UnauthorizedActionException(s"evil user ${sender.uid} can't update publisher $nid")

    for (newName <- name) {
      publisher.name = newName
    }

    for (newPic <- pic) {
      publisher.picSetter(newPic, metadata.client)
    }

    if (addUsers.nonEmpty) {
      publisher.addUser(addUsers)
    }

    if (removeUsers.nonEmpty) {
      publisher.removeUser(removeUsers)
    }

    if (addNodes.nonEmpty) {
      publisher.addNode(addNodes)
    }

    if (removeNodes.nonEmpty) {
      publisher.removeNode(removeNodes)
    }

    Yields.broadcast(publisher.users.filter(_ != sender)) {
      PublisherUpdateBrd(publisher.nid, publisher.name, publisher.pic, publisher.users, publisher.nodes)
    }

    PublisherUpdateRes()
  }
}

/** [[PublisherUpdate]] Result */
case class PublisherUpdateRes() extends Result

/**
  * [[PublisherUpdate]] broadcast
  * @param nid nid of updated publisher
  * @param name name of publisher
  * @param pic picture of publisher
  * @param users list of user after addition and deletion
  * @param nodes list of user after addition and deletion
  */
case class PublisherUpdateBrd(nid: NID, name: String, pic: Blob, users: Seq[UID], nodes: Seq[NID]) extends Broadcast