package yields.server.actions.nodes

import java.time.OffsetDateTime

import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Implicits._

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

    if (!(count > 0))
      throw new ActionArgumentException(s"negative count: $count")

    val node = Node(nid)
    val sender = metadata.client

    if (!node.users.contains(sender))
      throw new UnauthorizedActionException(s"$sender cannot get a message in $nid")

    val feed = node.getMessagesInRange(datetime, count).map {

      case (date, uid, Some(mediaRef), text) =>
        val media = Media(mediaRef)
        (date, uid, text, Some(media.contentType), Some(media.content), Some(media.nid))

      case (date, uid, None, text) =>
        (date, uid, text, None, None, None)

    }

    val (datetimes, senders, texts, contentTypes, contents, contentNids) = feed.unzip6
    NodeHistoryRes(nid, datetimes, senders, texts, contentTypes, contents, contentNids)

  }

}

/**
  * [[NodeHistory]] result. List are always share the same number of elements.
  * @param nid node nid
  * @param datetimes messages datetimes
  * @param senders messages senders
  * @param texts messages texts if any
  * @param contentTypes messages contentTypes if any
  * @param contents messages content if any
  */
case class NodeHistoryRes(nid: NID,
                          datetimes: List[OffsetDateTime],
                          senders: List[Identity],
                          texts: List[String],
                          contentTypes: List[Option[String]],
                          contents: List[Option[Blob]],
                          contentNids: List[Option[NID]]) extends Result