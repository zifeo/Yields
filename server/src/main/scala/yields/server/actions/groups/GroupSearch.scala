package yields.server.actions.groups

import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{NID, Node}
import yields.server.mpi.Metadata
import yields.server.dbi._


/**
  * Action searching for nodes matching the pattern
  * @param pattern pattern to satisfy
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
