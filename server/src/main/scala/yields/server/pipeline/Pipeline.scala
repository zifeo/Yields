package yields.server.pipeline

import akka.actor.ActorSystem
import akka.stream.io.Framing
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import yields.server.mpi.{Request, Response}
import yields.server.pipeline.blocks.{LoggerModule, SerializationModule}
import yields.server.utils.Config

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Pipeline creator object.
  */
object Pipeline {

  val parallelism = Config.getInt("pipeline.parallelism")
  val framesize = Config.getInt("pipeline.framesize")

  /**
    * Creates a pipeline including the following steps:
    *
    * {{{
    *                                         execute
    *          log     serialize     log     +dispatch
    *        +-----+    +-----+    +-----+    +-----+
    *  In ~> |     | ~> |     | ~> |     | ~> |     |
    * Out <~ |     | <~ |     | <~ |     | <~ |     |
    *        +-----+    +-----+    +-----+    +-----+
    * }}}
    *
    * @param system implicit actor system
    * @return flows pipeline
    */
  def apply()(implicit system: ActorSystem): Flow[ByteString, ByteString, Unit] = {

    implicit val logger = system.log

    val frame = Framing.delimiter(ByteString("\n"), maximumFrameLength = framesize, allowTruncation = false)

    val serialize = SerializationModule()
    val logMessage = LoggerModule[Request, Response]()

    val execute = Flow[Request].mapAsyncUnordered(parallelism) { case Request(action, metadata) =>
      Future {
        val result = action.run(metadata)
        Response(result, metadata.replied)
      }
    }

    Flow[ByteString]
      .via(frame)
      .via(
        serialize
          .atop(logMessage)
          .join(execute)
      )
  }

}
