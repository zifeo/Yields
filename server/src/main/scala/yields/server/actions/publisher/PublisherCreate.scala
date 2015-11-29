package yields.server.actions.publisher

import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{User, Publisher, UID, NID}
import yields.server.mpi.Metadata

/**
  * Create a publisher
  * @param name name of the publisher
  * @param users users that can publish
  * @param nodes subscribed nodes
  *
  *              TODO implement security rules for nodes
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

    val publisher = Publisher.createPublisher(name, metadata.client)
    publisher.addUser(users.toList)
    publisher.addNode(nodes.toList)
    PublisherCreateRes(publisher.nid)

  }
}

case class PublisherCreateRes(nid: NID) extends Result
