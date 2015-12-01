package yields.server.actions.publisher

import yields.server.Yields
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.{Broadcast, Result, Action}
import yields.server.dbi.models.{User, Publisher, UID, NID}
import yields.server.mpi.Metadata

/**
  * Create a publisher
  * @param name name of the publisher
  * @param users users that can publish
  * @param nodes subscribed nodes
  */
case class PublisherCreate(name: String, users: List[UID], nodes: List[NID]) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val user = User(metadata.client)
    val entourage = user.entourage
    val sender = user.uid
    val otherUsers = users.filter(_ != sender)

    if (! otherUsers.forall(entourage.contains))
      throw new UnauthorizedActionException(s"not all users are $sender's entourage: $users")

    // TODO: check public node

    val publisher = Publisher.create(name, sender)

    if (otherUsers.nonEmpty) {
      publisher.addUser(otherUsers)
    }

    if (nodes.nonEmpty) {
      publisher.addNode(nodes)
    }

    user.addGroup(publisher.nid)
    otherUsers.foreach(User(_).addGroup(publisher.nid))

    Yields.broadcast(otherUsers) {
      PublisherCreateBrd(publisher.nid, name, users, nodes)
    }

    PublisherCreateRes(publisher.nid)

  }
}

/**
  * [[PublisherCreate]] result
  * @param nid nid of new publisher
  */
case class PublisherCreateRes(nid: NID) extends Result

/**
  * [[PublisherCreate]] broadcast
  * @param nid nid of publisher broadcasting
  * @param name publisher name
  * @param users list of users
  * @param nodes list of nodes
  */
case class PublisherCreateBrd(nid: NID, name: String, users: List[UID], nodes: List[NID]) extends Broadcast
