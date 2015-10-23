package yields.server.actions.groups

import yields.server.models.{UID, Blob, GID}
import yields.server.actions.{Result, Action}

/**
 * Update a group given given specific fields.
 * @param gid group id
 * @param name new name
 * @param pic new profile image
 */
case class GroupUpdate(gid: GID, name: Option[String], pic: Option[Blob]) extends Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  override def run(sender: UID): Result = {
    GroupUpdateRes()
  }

}

/** [[GroupUpdate]] result. */
case class GroupUpdateRes() extends Result
