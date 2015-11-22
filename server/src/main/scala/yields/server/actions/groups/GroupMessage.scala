package yields.server.actions.groups

import java.time.OffsetDateTime

import yields.server.Yields
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Message related to a particular group.
  * @param nid group id
  * @param content message content
  */
case class GroupMessage(nid: NID, content: String) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (nid > 0) {
      if (!content.isEmpty) {
        val group = Group(nid)
        val c = (Temporal.now, metadata.client, None, content)
        group.addMessage(c)
        Yields.broadcast(group.users.filter(_ != metadata.client)) {
          GroupMessageRes(c._1)
        }
      } else {
        val errorMessage = getClass.getSimpleName
        throw new ActionArgumentException(s"Empty content in : $errorMessage")
      }
    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"Bad nid value in : $errorMessage")
    }
  }

}

/** [[GroupMessage]] result. */
case class GroupMessageRes(datetime: OffsetDateTime) extends Result
