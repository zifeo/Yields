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
      val split = content.partition(_._3.isDefined)

      // Get media from entries that have some
      val haveMedia = for {
        c <- split._1
        m = Media(c._3.get)
      } yield (c._1, c._2, Some(m.content), c._4)

      val dontHaveMedia = split._2.map(x => (x._1, x._2, None, x._4))

      NodeHistoryRes(nid, haveMedia ::: dontHaveMedia)

    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"Bad nid and/or count value in : $errorMessage")
    }
  }

}

/** [[NodeHistory]] result. */
case class NodeHistoryRes(nid: NID, nodes: Seq[ResponseFeedContent]) extends Result