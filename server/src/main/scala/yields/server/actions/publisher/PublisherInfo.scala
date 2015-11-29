package yields.server.actions.publisher

import yields.server.actions.exceptions.{UnauthorizedActionException}
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Publisher, User, UID, NID}
import yields.server.mpi.Metadata

/**
  * Get infos about a publisher
  * @param nid nid of the publisher
  *
  *            TODO get pic
  */
case class PublisherInfo(nid: NID) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val sender = User(metadata.client)
    if (!sender.groups.contains(nid))
      throw new UnauthorizedActionException("client who wants the infos must be a subscriber")

    val publisher = Publisher(nid)
    PublisherInfoRes(nid, publisher.name, None, publisher.users, publisher.nodes)
  }
}

case class PublisherInfoRes(nid: NID, name: String, pic: Option[Array[Byte]], users: Seq[UID], nodes: Seq[NID]) extends Result