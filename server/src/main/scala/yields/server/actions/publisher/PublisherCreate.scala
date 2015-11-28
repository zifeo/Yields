package yields.server.actions.publisher

import yields.server.actions.exceptions.ActionArgumentException
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
    if (name.nonEmpty) {
      val sender = User(metadata.client)
      val entourage = sender.entourage
      // Rules
      if (users.forall(entourage.contains)) {
        val publisher = Publisher.createPublisher(name)
        publisher.addMultipleUser(users)
        publisher.addMultipleNodes(nodes)
        PublisherCreateRes(publisher.nid)
      } else {
        throw new ActionArgumentException("users must be in sender's entourage")
      }
    } else {
      throw new ActionArgumentException("publisher name cannot be empty")
    }
  }
}

case class PublisherCreateRes(nid: NID) extends Result
