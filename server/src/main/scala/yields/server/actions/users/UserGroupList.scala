package yields.server.actions.users

import java.time.OffsetDateTime

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Group, NID, UID, User}
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
 * Lists the groups of the user.
 * @param uid user id
 */
case class UserGroupList(uid: UID) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {
    if (uid > 0) {
      val user = User(uid)
      val groups = user.groups.toSeq
      val list = groups.map(x => (x, "", Temporal.notYet))
      UserGroupListRes(list)
    } else {
      throw new ActionArgumentException("Bad uid")
    }

  }

}

/** [[UserGroupList]] result. */
case class UserGroupListRes(groups: Seq[(NID, String, OffsetDateTime)]) extends Result