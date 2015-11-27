package yields.server.actions.users

import yields.server.Yields
import yields.server.actions._
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi.models.{Blob, Email, UID, User}
import yields.server.mpi.Metadata

/**
  * Update an user given optional fields.
  * @param email new email address
  * @param name new name
  * @param image new profile image
  * TODO: set picture
  */
case class UserUpdate(email: Option[Email], name: Option[String], image: Option[Blob]) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val user = User(metadata.client)

    for (newEmail <- email) {
      if (! validEmail(newEmail))
        throw new ActionArgumentException(s"invalid email: $email")

      // TODO: allow email change (unneeded with Google login)
      // user.email = newEmail
    }

    for (newName <- name) {
      user.name = newName
    }

    Yields.broadcast(user.entourage) {
      UserUpdateBrd(user.uid, user.email, user.name, user.picture)
    }

    UserUpdateRes()
  }

}

/**
  * [[UserUpdate]] result.
  */
case class UserUpdateRes() extends Result

/**
  * [[UserUpdate]] broadcast.
  * @param uid user updated
  * @param email new or current user email
  * @param name new or current user name
  * @param pic new or current user pic
  */
case class UserUpdateBrd(uid: UID, email: String, name: String, pic: Blob) extends Broadcast