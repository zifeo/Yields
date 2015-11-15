package yields.server

import akka.actor.ActorSystem
import akka.io.Tcp.SO
import akka.stream.scaladsl.Tcp
import akka.stream.scaladsl.Tcp.IncomingConnection
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import yields.server.pipeline.Pipeline
import yields.server.utils.Config

import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

/**
  * Yields server daemon.
  */
object Yields extends App {

  // Starts system and enable flow errors logging
  implicit val system = ActorSystem("Yields-server")
  implicit val materializer = {
    val decider: Supervision.Decider = { case NonFatal(e) =>
      val exception = e.getStackTrace.toList.headOption.getOrElse("error when getting the stacktrace")
      val message = e.getMessage
      system.log.error(s"$exception: $message")
      Supervision.stop
    }
    ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))
  }

  // Setups components
  val connections = Tcp().bind(
    interface = Config.getString("addr"),
    port = Config.getInt("port"),
    options = List(SO.KeepAlive(on = false), SO.TcpNoDelay(on = false)),
    halfClose = false,
    idleTimeout = Duration.Inf
  )
  val pipeline = Pipeline()

  // Handle connections
  connections runForeach { case IncomingConnection(_, remoteAddress, flow) =>
    system.log.info(s"connection from $remoteAddress")
    flow.join(pipeline).run()
  }

}
