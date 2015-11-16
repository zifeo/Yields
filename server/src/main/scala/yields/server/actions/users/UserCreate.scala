package yields.server.actions.users

import java.util.regex.{Matcher, Pattern}

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{User, UID}
import yields.server.mpi.Metadata
import yields.server.actions._


/**
  * Action validating information and creating new user
  * @param email new user's email
  * @param name new user's name
  */
case class UserCreate(email: String, name: String) extends Action {

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
      } else {
        val errorMessage = getClass.getSimpleName
        throw new ActionArgumentException(s"invalid email in : $errorMessage")
      }
    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"empty email and/or name in : $errorMessage")
    }
  }

}

/**
  * [[UserCreate]] result.
  * @param uid uid of the user
  */
case class UserCreateRes(uid: UID) extends Result
