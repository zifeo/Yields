package yields.server.actions.users

import yields.server.actions._
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi.models.{Email, UID, User}
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

    if (! validEmail(email))
      throw new ActionArgumentException(s"invalid email: $email")

    val userLookup = User.fromEmail(email)
    val user = userLookup.getOrElse(User.create(email))
    user.connected()

    UserConnectRes(user.uid, userLookup.nonEmpty)

  }

}

/**
  * [[UserConnect]] result.
  * @param uid users uid
  * @param returning whether the user is returining or new one
  */
case class UserConnectRes(uid: UID, returning: Boolean) extends Result