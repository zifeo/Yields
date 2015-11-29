package yields.server.actions.publisher

import yields.server.actions.{Result, Action}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Get infos about a publisher
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
    PublisherInfoRes(nid, publisher.name, publisher.pic, publisher.users, publisher.nodes)
  }
}

/**
  * Publisher Info broadcast
  * @param nid nid of publisher broadcasting
  * @param name name of publisher
  * @param pic content of publisher's profile pic
  * @param users list of users
  * @param nodes list of nodes
  */
case class PublisherInfoRes(nid: NID, name: String, pic: Blob, users: Seq[UID], nodes: Seq[NID]) extends Result