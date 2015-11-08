package yields.server.actions.users

import yields.server.dbi.models.{UID, Email}
import yields.server.actions.{Result, Action}
import yields.server.mpi.Metadata

/**
 * Connects an user to the server.
 * @param email user mail
 */
case class UserConnect(email: Email) extends Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  override def run(metadata: Metadata): Result = {
    UserConnectRes(metadata.sender)
  }

}

/** [[UserConnect]] result. */
case class UserConnectRes(uid: UID) extends Result