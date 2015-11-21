package yields.server.actions.nodes

import java.time.OffsetDateTime

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import collection.JavaConversions._

/**
  * Fetch each group node starting from a date to a count.
  * @param nid node id
  * @param datetime last time related to the given node
  * @param count number of node wanted
  */

case class NodeHistory(nid: NID, datetime: OffsetDateTime, count: Int) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (nid > 0 && count > 0) {
      val group = Group(nid)
      val content: List[IncomingFeedContent] = group.getMessagesInRange(datetime, count)

      // Get media
      val contentWithMedia = content.map {
        case (d, u, Some(x), t) =>
          val m = Media(x)
          (d, u, Some(m.content), t)
        case (d, u, None, t) => (d, u, None, t)
      }

      NodeHistoryRes(nid, contentWithMedia)

    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"Bad nid and/or count value in : $errorMessage")
    }
  }

}

/** [[NodeHistory]] result. */
case class NodeHistoryRes(nid: NID, nodes: Seq[ResponseFeedContent]) extends Result