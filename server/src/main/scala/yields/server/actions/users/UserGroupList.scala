package yields.server.actions.users

import java.time.OffsetDateTime

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Group, NID, UID, User}
import yields.server.mpi.Metadata

/**
  * Lists the groups of the user.
  * @param uid user id
  */
case class UserGroupList(uid: UID) extends Action {

  lazy val errorMessage = getClass.getSimpleName

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (uid > 0) {
      val user = User(uid)
      val groups = user.groups.toSeq
      val list = groups.map(x => (x, Group(x).name, Group(x).updated_at))
      UserGroupListRes(list)
    } else {
      throw new ActionArgumentException(s"Bad uid in : $errorMessage")
    }

  }

}

/**
  * [[UserGroupList]] result.
  * @param groups sequence of nid, name and last activity
  */
case class UserGroupListRes(groups: Seq[(NID, String, OffsetDateTime)]) extends Result