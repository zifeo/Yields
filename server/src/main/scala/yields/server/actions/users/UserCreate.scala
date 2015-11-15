package yields.server.actions.users

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{User, UID}
import yields.server.mpi.Metadata


/**
  * Action validating information and creating new user
  * @param email new user's email
  * @param name new user's name
  *
  * TODO validate information
  */
case class UserCreate(email: String, name: String) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (!email.isEmpty && !name.isEmpty) {
      val u = User.create(email)
      if (u.uid > 0) {
        u.name = name
      } else throw new ActionArgumentException("error during creation")

      UserCreateRes(u.uid)
    } else throw new ActionArgumentException("empty email and/or name")
  }

}

/**
  * [[UserCreate]] result.
  * @param uid uid of the user
  */
case class UserCreateRes(uid: UID) extends Result