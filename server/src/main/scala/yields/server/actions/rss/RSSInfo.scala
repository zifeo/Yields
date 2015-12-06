package yields.server.actions.rss

import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Rss, NID}
import yields.server.mpi.Metadata

/**
  * Get rss info
  * @param nid nid
  */
case class RSSInfo(nid: NID) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val rss = Rss(nid)
    RSSInfoRes(rss.name, rss.url, rss.tags)
  }
}

/**
  * [[RSSInfo]] result
  * @param name rss name
  * @param url rss url
  * @param tags rss tags
  */
case class RSSInfoRes(name: String, url: String, tags: Set[String]) extends Result