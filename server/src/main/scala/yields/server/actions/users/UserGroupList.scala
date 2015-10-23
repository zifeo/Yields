package yields.server.actions.users

import yields.server.models.{Group, UID}
import yields.server.actions.{Result, Action}

/**
 * Lists the groups of the user.
 * @param uid user id
 */
case class UserGroupList(uid: UID) extends Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  override def run(sender: UID): Result = {
    UserGroupListRes(Seq.empty)
  }

}

/** [[UserGroupList]] result. */
case class UserGroupListRes(groups: Seq[Group]) extends Result