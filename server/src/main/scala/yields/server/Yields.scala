package yields.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import yields.server.actions.Action
import yields.server.actions.groups.GroupAction
import yields.server.pipeline.{ParserModule, LoggerModule}

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
  val strLoggerModule = LoggerModule[ByteString, ByteString]()
  val messageLoggerModule = LoggerModule[Action, Action]()
  val parserModule = ParserModule()
  val mockExecuteStep =
    Flow[Action].mapAsync {
      case GroupAction(gid, mess) => GroupAction(gid, "answered")
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
