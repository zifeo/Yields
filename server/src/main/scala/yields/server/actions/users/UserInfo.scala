package yields.server.actions.users

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{UID, User}
import yields.server.mpi.Metadata

/**
  * Get user entourage action
  * @param uid user
  */
case class UserInfo(uid: UID) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (uid > 0) {
      val u = User(uid)
      UserInfoRes(u.uid, u.name, u.email, u.entourage)
    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"bad uid in : $errorMessage")
    }
  }
}

/**
  * UserGetEntourage result
  * @param entourage sequence of uids corresponding to user's entourage
  */
case class UserInfoRes(uid: UID, name: String, email: String, entourage: Seq[UID]) extends Result
