package yields.server

import java.util.logging.LogManager

import akka.actor._
import akka.stream._
import yields.server.actions.Result
import yields.server.dbi.models.UID
import yields.server.pipeline.Pipeline
import yields.server.router.{Dispatcher, Router}

import scala.io.StdIn
import scala.util.control.NonFatal

/**
  * Yields server daemon.
  */
object Yields {

  { // Configure logging with LogBack
    val manager = LogManager.getLogManager
    manager.readConfiguration()
  }

  private implicit lazy val system = ActorSystem("Yields-server")
  private implicit lazy val materializer = {
    val decider: Supervision.Decider = {
      case NonFatal(e) =>
        val exception = e.getStackTrace.toList.headOption.getOrElse("error when getting the stacktrace")
        val message = e.getMessage
        system.log.error(s"$exception: $message")
        Supervision.stop
    }
    ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))
  }

  private lazy val pipeline = Pipeline()
  private lazy val dispatcher = system.actorOf(Dispatcher.props, "Yields-dispatcher")
  private lazy val router = system.actorOf(Router.props(pipeline, dispatcher), "Yields-router")

  /**
    * Launches the Yields app.
    * @param args no args
    */
  def main(args: Array[String]): Unit = {
    start()
  }

  /**
    * Broadcast given result to all uid using the dispatcher.
    * @param uids uid to receive the broadcast
    * @param result result to be broacasted
    * @return result broadcasted
    */
  def broadcast(uids: Seq[UID])(result: Result): Result = {
    import Dispatcher._
    Yields.dispatcher ! Notify(uids, result)
    result
  }

  /**
    * Starts the server.
    * @return empty future representing server liveness
    */
  private[server] def start(): Unit = {
    system.log.info("Server starting.")
    system
    materializer
    dispatcher
    router
    system.log.info("Server started.")
  }

  /**
    * Closes the server (cannot be restart without full restart).
    * This include the actor system and the database.
    */
  private[server] def close(): Unit = {
    system.log.info("Server closing.")
    system.terminate()
    system.log.info("Server closed.")
  }

}
