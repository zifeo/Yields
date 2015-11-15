package yields.server.actions.users

import yields.server.actions.exceptions.UnauthorizeActionException
import yields.server.dbi.models.{User, UID, Email}
import yields.server.actions.{Result, Action}
import yields.server.mpi.Metadata

/**
 * Connects an user to the server.
 * @param email user mail
 */
case class UserConnect(email: Email) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {

    User.fromEmail(email) match {
      case Some(user) => UserConnectRes(user.uid)
      case _ => throw new UnauthorizeActionException("Invalid email")
    }

  }

}

/**
  * [[UserConnect]] result.
  * @param uid users uid
  */
case class UserConnectRes(uid: UID) extends Result