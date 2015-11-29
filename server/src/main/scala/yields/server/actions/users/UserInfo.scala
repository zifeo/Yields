package yields.server.actions.users

import java.time.OffsetDateTime

import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Email, UID, User}
import yields.server.mpi.Metadata

/**
  * Returns all user information.
  * Either the client is the user and gets all, or only a part of it if belonging to user entourage.
  * @param uid user
  */
case class UserInfo(uid: UID) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val user = User(uid)
    user.hydrate()

    metadata.client match {
      case `uid` =>
        val (entourage, entourageUpdates) = user.entourageWithUpdates.unzip
        UserInfoRes(user.uid, user.name, user.email, entourage, entourageUpdates)

      case sender if user.entourage.contains(sender) =>
        UserInfoRes(user.uid, user.name, user.email, Seq.empty, Seq.empty)

      case _ =>
        throw new UnauthorizedActionException(s"client not related to uid: $uid")
    }

  }

}

/**
  * [[UserInfo]] result.
  * @param uid user
  * @param name user name
  * @param email user email
  * @param entourage user entourage if self requested
  * @param entourageUpdatedAt user entourage last updates if self requested
  */
case class UserInfoRes(uid: UID,
                       name: String,
                       email: Email,
                       entourage: Seq[UID],
                       entourageUpdatedAt: Seq[OffsetDateTime]) extends Result
