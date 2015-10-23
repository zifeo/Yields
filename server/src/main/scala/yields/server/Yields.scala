package yields.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import yields.server.actions.Action
import yields.server.actions.groups.GroupMessage
import yields.server.pipeline.{ParserModule, LoggerModule}
import yields.server.utils.Config

/**
 * Yields server daemon.
 */
object Yields extends App {

  implicit val system = ActorSystem("Yields-server")
  implicit val materializer = ActorMaterializer()
  implicit val logger = system.log

  // Port mapping
  val connections = Tcp().bind(Config.getString("addr"), Config.getInt("port"))

  // Modules creation
  val strLoggerModule = LoggerModule[ByteString, ByteString]()
  val messageLoggerModule = LoggerModule[Action, Action]()
  val parserModule = ParserModule()
  val mockExecuteStep =
    Flow[Action].map {
      case GroupMessage(gid, mess) => GroupMessage(gid, "answered")
      case x => x
    }

  // Pipeline setup
  val pipeline =
    strLoggerModule
      .atop(parserModule)
      .atop(messageLoggerModule)
      .join(mockExecuteStep)

  // Handle incoming connections
  connections runForeach { connection =>
    logger.info(s"[CONNECT] ${connection.remoteAddress}")
    connection.handleWith(pipeline)
  }

}
