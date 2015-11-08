package yields.server.actions.users

import java.time.OffsetDateTime

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{NID, Group, UID}
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
case class UserGroupListRes(groups: Seq[(NID, String, OffsetDateTime)]) extends Result