package yields.server.actions.publisher

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Publisher, UID, NID}
import yields.server.mpi.Metadata

/**
  * Create a publisher
  * @param name name of the publisher
  * @param users users that can publish
  * @param nodes subscribed nodes
  *
  * TODO implement security rules for users and nodes
  */
case class PublisherCreate(name: String, users: Seq[UID], nodes: Seq[NID]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (name.nonEmpty) {
      val publisher = Publisher.createPublisher(name)
      publisher.addMultipleUser(users)
      publisher.addMultipleNodes(nodes)
      PublisherCreateRes(publisher.nid)
    } else {
      throw new ActionArgumentException("publisher name cannot be empty")
    }
  }
}

case class PublisherCreateRes(nid: NID) extends Result
