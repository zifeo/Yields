package yields.server.actions.rss

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Rss, NID}
import yields.server.mpi.Metadata
import yields.server.actions._

/**
  * Create a new RSS nod
  * @param name rss name
  * @param url rss url
  */
case class RSSCreate(name: String, url: String, tags: Seq[String]) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (name.isEmpty || url.isEmpty || validURL(url))
      throw new ActionArgumentException("Rss create : bad name and/or url")

    val rss = Rss.create(name, url)

    if (tags.nonEmpty) {
      rss.addTags(tags)
    }
    RSSCreateRes(rss.nid)
  }
}

/**
  * [[RSSCreate]] Result
  * @param nid nid of new RSS
  */
case class RSSCreateRes(nid: NID) extends Result