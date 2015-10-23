package yields.server.actions.users

import yields.server.models.{UID, Email}
import yields.server.actions.{Result, Action}

/**
 * Connects an user to the server.
 * @param email user mail
 */
case class UserConnect(email: Email) extends Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  def run(sender: UID): Result = {
    UserConnectRes(sender)
  }

}

/** [[UserConnect]] result. */
case class UserConnectRes(uid: UID) extends Result