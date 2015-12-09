package yields.server.actions.rss

import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{NID, RSS}
import yields.server.mpi.Metadata

/**
  * Get rss info.
  * @param nid nid
  */
case class RSSInfo(nid: NID) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {

    val rss = RSS(nid)
    RSSInfoRes(rss.name, rss.url, rss.filter, rss.tags)

  }

}

/**
  * [[RSSInfo]] result.
  * @param name rss name
  * @param url rss url
  * @param tags rss tags
  */
case class RSSInfoRes(name: String, url: String, filter: String, tags: Set[String]) extends Result
