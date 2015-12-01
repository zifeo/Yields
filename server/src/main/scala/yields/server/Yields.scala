package yields.server

import java.util.logging.LogManager

import akka.actor._
import akka.stream.Supervision.{Resume, Stop}
import akka.stream._
import yields.server.actions.{Broadcast, Result}
import yields.server.dbi.models.UID
import yields.server.pipeline.Pipeline
import yields.server.router.{Dispatcher, Router}

import scala.util.control.NonFatal

/**
  * Yields server daemon.
  */
object Yields {

  LogManager.getLogManager.readConfiguration()

  private implicit lazy val system = ActorSystem("Yields-server")
  private implicit lazy val materializer = {
    val decider: Supervision.Decider = {
      case NonFatal(nonfatal) =>
        val message = nonfatal.getMessage
        system.log.error(nonfatal, s"pipeline non fatal: $message")
        Resume
      case fatal =>
        val message = fatal.getMessage
        system.log.error(fatal, s"pipeline fatal: $message")
        Stop
    }
    ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))
  }

  private lazy val dispatcher = system.actorOf(Dispatcher.props, "Yields-dispatcher")
  private lazy val router = system.actorOf(Router.props(Pipeline(), dispatcher), "Yields-router")

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
  def broadcast(uids: List[UID])(bcast: Broadcast): Unit = {
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
  }

  /**
    * Closes the server (cannot be restart without full restart).
    * This include the actor system and the database.
    */
  private[server] def stop(): Unit = {
    system.terminate()
  }

}
