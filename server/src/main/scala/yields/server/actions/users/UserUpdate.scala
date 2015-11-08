package yields.server.actions.users

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Blob, Email, UID}
import yields.server.mpi.Metadata

/**
 * Update an user given optional fields.
 * @param uid user id
 * @param email new email address
 * @param name new name
 * @param image new profile image
 */
case class UserUpdate(uid: UID, email: Option[Email], name: Option[String], image: Option[Blob]) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {
    UserUpdateRes()
  }

}

/** [[UserUpdate]] result. */
case class UserUpdateRes() extends Result