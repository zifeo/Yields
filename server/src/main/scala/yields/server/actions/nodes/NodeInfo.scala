package yields.server.actions.nodes

import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.groups.GroupInfo
import yields.server.actions.publisher.PublisherInfo
import yields.server.actions.rss.RSSInfo
import yields.server.actions.{Result, Action}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Get info about a node.
  * @param nid nid of the publisher
  */
case class NodeInfo(nid: NID) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val sender = metadata.client
    Node(nid).kind match {
      case "Publisher" => PublisherInfo(nid).run(metadata)
      case "RSS" => RSSInfo(nid).run(metadata)
      case "Group" => GroupInfo(nid).run(metadata)
      case otherKind => throw new UnauthorizedActionException(s"$sender cannot get info on $nid ($otherKind)")
    }
    
  }

}
