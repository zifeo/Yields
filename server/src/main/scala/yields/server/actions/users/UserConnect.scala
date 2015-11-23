package yields.server.actions.users

import yields.server.actions._
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi.models.{Email, UID, User}
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

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
          user.connected_at = Temporal.now
          UserConnectRes(user.uid)
        case None =>
          val newUser = User.create(email)
          newUser.connected_at = Temporal.now
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