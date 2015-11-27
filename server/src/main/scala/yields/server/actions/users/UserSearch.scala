package yields.server.actions.users

import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Email, User, UID}
import yields.server.mpi.Metadata

/**
  * Looks if there is a corresponding user email and returns its information.
  * @param email lookup email
  */
case class UserSearch(email: Email) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val lookup = User.fromEmail(email)
    val uid = lookup.map(_.uid).getOrElse(0: UID)
    UserSearchRes(uid)

  }

}

/**
  * [[UserSearch]] result.
  * @param uid corresponding user uid if existing or 0 on failure
  */
case class UserSearchRes(uid: UID) extends Result