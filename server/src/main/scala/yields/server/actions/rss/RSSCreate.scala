package yields.server.actions.rss

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Rss, NID}
import yields.server.mpi.Metadata

case class RSSCreate(name: String, url: String) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (name.nonEmpty && url.nonEmpty) {
      val rss = Rss.createRss(name, url)
      RSSCreateRes(rss.nid)
    } else {
      throw new ActionArgumentException("Rss create : bad name and/or url")
    }
  }
}

case class RSSCreateRes(nid: NID) extends Result