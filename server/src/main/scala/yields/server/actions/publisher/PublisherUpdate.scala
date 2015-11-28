package yields.server.actions.publisher

import java.sql.Blob
import javax.sound.sampled.UnsupportedAudioFileException

import yields.server.actions.exceptions.UnauthorizeActionException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Publisher, User, UID, NID}
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
  * TODO update picture
  */
case class PublisherUpdate(nid: NID, name: Option[String], pic: Option[Array[Byte]], addUsers: Seq[UID],
                      removeUsers: Seq[UID], addNodes: Seq[NID], removeNodes: Seq[NID]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val sender = User(metadata.client)
    if (sender.groups.contains(nid)) {
      val publisher = Publisher(nid)
      if (publisher.users.contains(metadata.client)) {
        if (name.isDefined) {
          if (name.get.nonEmpty) {
            publisher.name = name.get
          }
        }
        if (pic.isDefined) {

        }
        PublisherUpdateRes()
      } else {
        throw new UnauthorizeActionException("the publisher can only be updated by a user who can publish")
      }
    } else {
      throw new UnauthorizeActionException("the sender must have the publisher in his groups")
    }
  }
}

case class PublisherUpdateRes() extends Result
