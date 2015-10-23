package yields.server.actions.users

import yields.server.actions.{Action, Result}
import yields.server.models.{Blob, Email, UID}

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
   * @param sender action requester
   * @return action result
   */
  override def run(sender: UID): Result = {
    UserUpdateRes()
  }

}

/** [[UserUpdate]] result. */
case class UserUpdateRes() extends Result