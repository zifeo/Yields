package yields.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import yields.server.pipeline.LoggingModule

/**
 * Yields server daemon.
 */
object Yields extends App {

  implicit val system = ActorSystem("Yields-server")
  implicit val materializer = ActorMaterializer()
  implicit val logger = system.log
  implicit val config = ConfigFactory.load().getConfig("yields")

  // Port mapping
  val connections = Tcp().bind(config.getString("addr"), config.getInt("port"))

  // Modules creation
  val loggingModule = LoggingModule[ByteString, ByteString]
  val mockExecuteStep =
    Flow[ByteString]
    .map(_.utf8String)
    .map(_ + " was server answered !\n")
    .map(ByteString(_))

  // Pipeline setup
  val pipeline =
    loggingModule
    .join(mockExecuteStep)

  // Handle incoming connections
  connections runForeach { connection =>
    logger.info(s"[CONNECT] ${connection.remoteAddress}")
    connection.handleWith(pipeline)
  }
  
}
