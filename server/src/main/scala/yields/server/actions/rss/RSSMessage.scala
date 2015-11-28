package yields.server.actions.rss

import yields.server.actions.{Result, Action}
import yields.server.mpi.Metadata

/**
  * Broadcast a rss message
  */
case class RSSMessage() extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = ???
}
