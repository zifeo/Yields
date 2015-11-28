package yields.server.actions.nodes

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Blob, NID}
import yields.server.mpi.Metadata


/**
  * Action searching for nodes matching the pattern
  * @param pattern pattern to satisfy
  */
case class NodeSearch(pattern: String) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    // TODO: look for publishers through tags and names

    NodeSearchRes(List.empty, List.empty, List.empty)
  }

}

/**
  * [[NodeSearch]] result. List always share the same number of elements.
  * @param nodes sequence of nid matching the pattern
  * @param names sequence of names matching the pattern
  * @param pic sequence of pic matching the pattern
  */
case class NodeSearchRes(nodes: List[NID], names: List[String], pic: Seq[Blob]) extends Result
