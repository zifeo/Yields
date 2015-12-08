package yields.server.actions.rss

import yields.server.actions.{Action, Result, _}
import yields.server.dbi.models.{NID, RSS}
import yields.server.mpi.Metadata

/**
  * Create a new RSS nod
  * @param name rss name
  * @param url rss url
  */
case class RSSCreate(name: String, url: String, filter: String, tags: Seq[String]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val nid =
      if (!validRSS(url)) 0l
      else {
        val rss = RSS.create(name, url, filter)
        if (tags.nonEmpty) {
          rss.addTags(tags)
        }
        rss.nid
      }

    RSSCreateRes(nid)

  }
}

/**
  * [[RSSCreate]] Result
  * @param nid nid of new RSS
  */
case class RSSCreateRes(nid: NID) extends Result