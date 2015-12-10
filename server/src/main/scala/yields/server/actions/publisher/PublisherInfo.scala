package yields.server.actions.publisher

import yields.server.actions.{Result, Action}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Get info about a publisher.
  * @param nid nid of the publisher
  */
case class PublisherInfo(nid: NID) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val publisher = Publisher(nid)
    val sender = metadata.client

    val users =
      if (publisher.users.contains(sender)) publisher.users
      else List.empty

    publisher.hydrate()
    PublisherInfoRes(nid, publisher.name, publisher.pic, users, publisher.nodes, publisher.tags)

  }

}

/**
  * Publisher info result.
  * @param nid nid of publisher broadcasting
  * @param name name of publisher
  * @param pic content of publisher's profile pic
  * @param users list of users
  * @param nodes list of nodes
  */
case class PublisherInfoRes(nid: NID, name: String, pic: Blob, users: List[UID], nodes: List[NID], tags: Set[String]) extends Result
