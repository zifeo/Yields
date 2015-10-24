package yields.server

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Tcp}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.util.ByteString
import yields.server.mpi.{Metadata, Request, Response}
import yields.server.pipeline.{LoggerModule, SerializationModule}
import yields.server.utils.{Config, Helpers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn

/**
 * Yields server daemon.
 */
object Yields extends App {

  implicit val system = ActorSystem("Yields-server")
  implicit val logger = system.log
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy({
    case e =>
      logger.error(e.getMessage)
      Supervision.Stop
  }: Supervision.Decider))

  // Port mapping
  val connections = Tcp().bind(Config.getString("addr"), Config.getInt("port"))

  // Modules creation
  val strLoggerModule = LoggerModule[ByteString, ByteString]()
  val messageLoggerModule = LoggerModule[Request, Response]()
  val parserModule = SerializationModule()
  val mockExecuteStep =
    Flow[Request].mapAsyncUnordered(2) { case Request(action, metadata) =>
      Future {
        val result = action.run(metadata.sender)
        Response(result, Metadata(7777, Helpers.currentDatetime))
      }
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

  // Kindly terminate
  StdIn.readLine()
  system.terminate()

}
