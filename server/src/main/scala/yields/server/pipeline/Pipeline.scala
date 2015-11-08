package yields.server.pipeline

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import yields.server.mpi.{Metadata, Request, Response}
import yields.server.pipeline.modules.{LoggerModule, SerializationModule}
import yields.server.utils.{Temporal, Config}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Pipeline creator object.
 */
object Pipeline {

  val parallelism = Config.getInt("pipeline.parallelism")

  /** Returns current timed server metadata. */
  def currentServerMetadata(): Metadata =
   Metadata(7777, Temporal.currentDatetime)

  /**
   * Creates a pipeline including the following steps:
   *
   *          log     serialize     log      execute
   *        +-----+    +-----+    +-----+    +-----+
   *  In ~> |     | ~> |     | ~> |     | ~> |     |
   * Out <~ |     | <~ |     | <~ |     | <~ |     |
   *        +-----+    +-----+    +-----+    +-----+
   *
   * @param system implicit actor system
   * @return flows pipeline
   */
  def apply()(implicit system: ActorSystem): Flow[ByteString, ByteString, Unit] = {

    implicit val logger = system.log

    val logIO = LoggerModule[ByteString, ByteString]()
    val serialize = SerializationModule()
    val logMessage = LoggerModule[Request, Response]()

    val execute = Flow[Request].mapAsyncUnordered(parallelism) { case Request(action, metadata) =>
      Future {
        val result = action.run(metadata)
        Response(result, currentServerMetadata())
      }
    }

    logIO
      .atop(serialize)
      .atop(logMessage)
      .join(execute)
  }

}
