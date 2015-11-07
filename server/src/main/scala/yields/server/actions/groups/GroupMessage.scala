package yields.server.actions.groups

import yields.server.actions.exceptions.BadFeedContentException
import yields.server.dbi.models._
import yields.server.actions.{Result, Action}

/**
 * Message related to a particular group.
 * @param nid group id
 * @param content message content
 */
case class GroupMessage(nid: NID, content: FeedContent) extends Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  override def run(sender: UID): Result = {
    if (checkContent) {
      val n = new Node(nid)
      n.addMessage(content)
      GroupMessageRes()
    } else {
      throw new BadFeedContentException("Invalid UID and/or NID")
    }
  }

  def checkContent: Boolean =
    content._1 < 0 && (content._2 != "" || content._3 > 0)

}

/** [[GroupMessage]] result. */
case class GroupMessageRes() extends Result
