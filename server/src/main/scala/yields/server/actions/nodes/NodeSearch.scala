package yields.server.actions.nodes

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Node, Blob, NID}
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

    val query = Node.fromName(pattern).map(node => (node.nid, node.name, node.pic))
    val (nids, names, pics) = query.toList.unzip3
    NodeSearchRes(nids, names, pics)
    
  }

}

/**
  * [[NodeSearch]] result. List always share the same number of elements.
  * @param nodes sequence of nid matching the pattern
  * @param names sequence of names matching the pattern
  * @param pics sequence of pic matching the pattern
  */
case class NodeSearchRes(nodes: List[NID], names: List[String], pics: Seq[Blob]) extends Result
