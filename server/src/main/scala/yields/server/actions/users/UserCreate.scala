package yields.server.actions.users

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result, _}
import yields.server.dbi.models.{UID, User}
import yields.server.mpi.Metadata


/**
  * Action validating information and creating new user
  * @param email new user's email
  * @param name new user's name
  */
case class UserCreate(email: String, name: String) extends Action {

  lazy val errorMessage = getClass.getSimpleName

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (!email.isEmpty && !name.isEmpty) {
      if (checkValidEmail(email)) {
        val u = User.create(email)
        u.name = name

        UserCreateRes(u.uid)
      } else throw new ActionArgumentException(s"invalid email in : $errorMessage")
    } else throw new ActionArgumentException(s"empty email and/or name in : $errorMessage")
  }

}

/**
  * [[UserCreate]] result.
  * @param uid uid of the user
  */
case class UserCreateRes(uid: UID) extends Result
