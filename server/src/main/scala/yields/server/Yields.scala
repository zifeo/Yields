package yields.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import yields.server.pipeline.LoggingModule

/**
 * Yields server daemon.
 */
object Yields extends App {

  implicit val system = ActorSystem("Yields-server")
  implicit val logger = system.log
  implicit val materializer = ActorMaterializer()

  val connections = Tcp().bind("127.0.0.1", 7777)

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
    logger.info(s"Connection from: ${connection.remoteAddress}")
    connection.handleWith(pipeline)
  }
  
}
