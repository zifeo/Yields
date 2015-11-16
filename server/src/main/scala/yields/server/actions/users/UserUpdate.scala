package yields.server.actions.users

import yields.server.actions._
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi.models.{Blob, Email, UID, User}
import yields.server.mpi.Metadata

/**
  * Update an user given optional fields.
  * @param uid user id
  * @param email new email address
  * @param name new name
  * @param image new profile image
  *
  *              TODO: Set image
  */
case class UserUpdate(uid: UID, email: Option[Email], name: Option[String], image: Option[Blob]) extends Action {

  lazy val errorMessage = getClass.getSimpleName

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (uid > 0) {
      val user = User(uid)

      if (email.isDefined) {
        if (checkValidEmail(email.get)) {
          user.email = email.get
        } else throw new ActionArgumentException(s"Invalid email in : $errorMessage")
      }
      if (name.isDefined) {
        user.name = name.get
      }

      UserUpdateRes()
    } else {
      throw new ActionArgumentException(s"Invalid uid in : $errorMessage")
    }
  }

}

/** [[UserUpdate]] result. */
case class UserUpdateRes() extends Result