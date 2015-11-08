package yields.server.actions.users

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Group, UID}
import yields.server.mpi.Metadata

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
    UserGroupListRes(Seq.empty)
  }

}

/** [[UserGroupList]] result. */
case class UserGroupListRes(groups: Seq[Group]) extends Result