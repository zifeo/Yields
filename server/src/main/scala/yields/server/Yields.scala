package yields.server

import java.util.logging.LogManager

import akka.actor.ActorSystem
import akka.io.Tcp.SO
import akka.stream.scaladsl.Tcp
import akka.stream.scaladsl.Tcp.IncomingConnection
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import yields.server.pipeline.Pipeline
import yields.server.utils.Config

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.control.NonFatal

/**
  * Yields server daemon.
  */
object Yields {

  // Configure logging with LogBack
  {
    val manager = LogManager.getLogManager
    manager.readConfiguration()
  }

  // Starts system and enable flow errors logging on demand
  private implicit lazy val system = ActorSystem("Yields-server")
  private implicit val materializer = {
    val decider: Supervision.Decider = {
      case NonFatal(e) =>
        val exception = e.getStackTrace.toList.headOption.getOrElse("error when getting the stacktrace")
        val message = e.getMessage
        system.log.error(s"$exception: $message")
        Supervision.stop
    }
    ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))
  }

  /**
    * Launches the Yields app.
    * @param args no args
    */
  def main(args: Array[String]): Unit = {
    start()
    StdIn.readLine() // get rid of remaining input buffer
    StdIn.readLine() // wait on a new line for stopping
    close()
    dbi.close()
  }

  /**
    * Starts the server.
    * @return empty future representing server liveness
    */
  private[server] def start(): Future[Unit] = {

    system.log.info("Server starting.")

    // Setup networking and pipeline.
    val connections = Tcp().bind(
      interface = Config.getString("addr"),
      port = Config.getInt("port"),
      options = List(SO.KeepAlive(on = false), SO.TcpNoDelay(on = true)),
      halfClose = false,
      idleTimeout = Duration.Inf
    )
    val pipeline = Pipeline()

    // Handles connections.
    connections runForeach { case IncomingConnection(_, remoteAddress, flow) =>
      system.log.info(s"connection from $remoteAddress")
      flow.join(pipeline).run()
    }
  }

  /**
    * Closes the server (cannot be restart without full restart).
    * This include the actor system and the database.
    */
  private[server] def close(): Unit = {
    system.log.info("Server closing.")
    system.terminate()
  }

}
