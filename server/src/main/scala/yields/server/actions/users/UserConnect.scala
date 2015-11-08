package yields.server.actions.users

import yields.server.actions.exceptions.BadLoginException
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
    val user = User.fromEmail(email)

    if (user.uid >= 0)
      UserConnectRes(user.uid)
    else
      throw new BadLoginException("Invalid email")
  }

}

/** [[UserConnect]] result. */
case class UserConnectRes(uid: UID) extends Result