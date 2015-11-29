package yields.server.actions.publisher

import yields.server.Yields
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.groups.GroupCreateBrd
import yields.server.actions.{Broadcast, Result, Action}
import yields.server.dbi.models.{User, Publisher, UID, NID}
import yields.server.mpi.Metadata

/**
  * Create a publisher
  * @param name name of the publisher
  * @param users users that can publish
  * @param nodes subscribed nodes
  */
case class PublisherCreate(name: String, users: Seq[UID], nodes: Seq[NID]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (name.isEmpty)
      throw new ActionArgumentException("publisher name cannot be empty")

    val sender = User(metadata.client)
    val entourage = sender.entourage

    if (!users.forall(entourage.contains))
      throw new UnauthorizedActionException("users must be in sender's entourage")

    val publisher = Publisher.create(name, metadata.client)

    publisher.addUser(metadata.client :: users.toList)

    if (nodes.nonEmpty) {
      publisher.addNode(nodes.toList)
    }

    Yields.broadcast(publisher.users.filter(_ != sender)) {
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
case class PublisherCreateBrd(nid: NID, name: String, users: Seq[UID], nodes: Seq[NID]) extends Broadcast
