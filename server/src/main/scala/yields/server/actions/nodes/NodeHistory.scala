package yields.server.actions.nodes

import java.time.OffsetDateTime

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata

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
      val content: List[FeedContent] = group.getMessagesInRange(datetime, count)
      val resMedia = for {
        c: FeedContent <- content
        m: Media <- Media(c._3.get)
        if c._3.isDefined
      } yield (c._1, c._2, m.content, c._4)

      NodeHistoryRes(nid, content)

    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"Bad nid and/or lastTid and/or count value in : $errorMessage")
    }
  }

}

/** [[NodeHistory]] result. */
case class NodeHistoryRes(nid: NID, nodes: Seq[FeedContent]) extends Result