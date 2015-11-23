package yields.server.actions.users

import yields.server.actions.exceptions.{ActionArgumentException, UnauthorizeActionException}
import yields.server.dbi.models.{User, UID, Email}
import yields.server.actions.{Result, Action}
import yields.server.mpi.Metadata
import yields.server.utils.Temporal
import yields.server.actions._

/**
  * Connects an user to the server.
  * @param email user mail
  *
  *              TODO Update timestamp
  */
case class UserConnect(email: Email) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (checkValidEmail(email)) {
      User.fromEmail(email) match {
        case Some(user) =>
          user.connected_at = Temporal.current
          UserConnectRes(user.uid)
        case None =>
          val newUser = User.create(email)
          newUser.connected_at = Temporal.current
          UserConnectRes(newUser.uid)
      }
    } else {
      val errorMessage = this.getClass.getSimpleName
      throw new ActionArgumentException(s"invalid email in : $errorMessage")
    }
  }

}

/**
  * [[UserConnect]] result.
  * @param uid users uid
  */
case class UserConnectRes(uid: UID) extends Result