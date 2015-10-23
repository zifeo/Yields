package yields.server.actions.groups

import yields.server.models.{UID, GID}
import yields.server.actions.{Result, Action}

/**
 * Message related to a particular group.
 * @param gid group id
 * @param content message content
 */
case class GroupMessage(gid: GID, content: String) extends Action {
  
  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  def run(sender: UID): Result = {
    GroupMessageRes()
  }
  
}

/** [[GroupMessage]] result. */
case class GroupMessageRes() extends Result
