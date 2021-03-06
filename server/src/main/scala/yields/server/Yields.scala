package yields.server

import java.util.logging.LogManager

import akka.actor._
import akka.stream._
import yields.server.actions.Broadcast
import yields.server.dbi.models.UID
import yields.server.pipeline.Pipeline
import yields.server.router.{Dispatcher, Router}
import yields.server.rss.RSSPooler
import yields.server.utils.FaultTolerance._

/**
  * Yields server daemon.
  */
object Yields {

  LogManager.getLogManager.readConfiguration()

  private implicit lazy val system = ActorSystem("Yields-server")
  private implicit lazy val materializer =
    ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(nonFatalResumeOrStopStream(system.log)))

  private lazy val dispatcher = system.actorOf(Dispatcher.props, "Yields-dispatcher")
  private lazy val router = system.actorOf(Router.props(Pipeline(), dispatcher), "Yields-router")
  private lazy val rsspooler = system.actorOf(RSSPooler.props, "Yields-rsspooler")

  /**
    * Launches the Yields app.
    * @param args no args
    */
  def main(args: Array[String]): Unit = {
    start()
    system.registerOnTermination {
      dbi.close()
    }
  }

  /**
    * Broadcast given result to all uid using the dispatcher.
    * @param uids uid to receive the broadcast
    * @param bcast result to be broacasted
    * @return result broadcasted
    */
  private[server] def broadcast(uids: List[UID])(bcast: Broadcast): Unit = {
    import Dispatcher._
    if (uids.nonEmpty) {
      Yields.dispatcher ! Notify(uids, bcast)
    }
  }

  /**
    * Starts the server.
    * @return empty future representing server liveness
    */
  private[server] def start(): Unit = {
    dispatcher
    router
    rsspooler
  }

  /**
    * Closes the server (cannot be restart without full restart).
    * This include the actor system and the database.
    */
  private[server] def stop(): Unit = {
    system.terminate()
  }

}
