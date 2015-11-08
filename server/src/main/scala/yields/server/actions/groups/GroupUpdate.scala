package yields.server.actions.groups

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Blob, NID}
import yields.server.mpi.Metadata

/**
 * Update a group given given specific fields.
 * @param nid group id
 * @param name new name
 * @param pic new profile image
 */
case class GroupUpdate(nid: NID, name: Option[String], pic: Option[Blob]) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {
    GroupUpdateRes()
  }

}

/** [[GroupUpdate]] result. */
case class GroupUpdateRes() extends Result
