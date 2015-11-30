package yields.server.actions.users

import java.time.OffsetDateTime

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Group, NID, UID, User}
import yields.server.mpi.Metadata

/**
  * Lists the groups of the user.
  */
case class UserGroupList() extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

      val user = User(metadata.client)
      val (groups, updates, refreshes) = user.groupsWithUpdates.unzip3
      UserGroupListRes(groups, updates, refreshes)

  }

}

/**
  * [[UserGroupList]] result.
  * @param groups sequence of nid, name and last activity
  */
case class UserGroupListRes(groups: Seq[NID],
                            updatedAt: Seq[OffsetDateTime],
                            refreshedAt: Seq[OffsetDateTime]) extends Result