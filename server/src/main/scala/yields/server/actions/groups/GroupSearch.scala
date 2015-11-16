package yields.server.actions.groups

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.NID
import yields.server.mpi.Metadata


/**
  * Action searching for nodes matching the pattern
  * @param pattern pattern to satisfy
  *
  *                TODO implementation
  */
case class GroupSearch(pattern: String) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    GroupSearchRes(List())
  }

}


/**
  * [[GroupSearch]] result.
  * @param res sequence of nid and group names matching the pattern
  */
case class GroupSearchRes(res: Seq[(NID, String)]) extends Result
