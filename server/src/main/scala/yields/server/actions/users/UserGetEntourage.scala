package yields.server.actions.users

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{User, UID}
import yields.server.mpi.Metadata

/**
  * Get user entourage action
  * @param uid user
  */
case class UserGetEntourage(uid: UID) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (uid > 0) {
      val u = User(uid)
      UserGetEntourageRes(u.entourage)
    } else throw new ActionArgumentException(s"bad uid in : ${this.getClass.getSimpleName}")
  }
}

/**
  * UserGetEntourage result
  * @param entourage sequence of uids corresponding to user's entourage
  */
case class UserGetEntourageRes(entourage: Seq[(UID)]) extends Result
