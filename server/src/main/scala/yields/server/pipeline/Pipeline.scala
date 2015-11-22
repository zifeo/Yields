package yields.server.pipeline

import akka.actor.ActorSystem
import akka.stream.io.Framing
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import yields.server.mpi.{Metadata, Request, Response}
import yields.server.pipeline.blocks.{DispatchStep, LoggerModule, SerializationModule}
import yields.server.utils.{Temporal, Config}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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

    val logIO = LoggerModule[ByteString, ByteString]()
    val serialize = SerializationModule()
    val logMessage = LoggerModule[Request, Response]()
    val dispatch = DispatchStep()

    val execute = Flow[Request].mapAsyncUnordered(parallelism) { case Request(action, metadata) =>
      Future {
        val result = action.run(metadata)
        Response(result, metadata.replied)
      }
    }

    Flow[ByteString]
      .via(frame)
      .via(
        logIO
          .atop(serialize)
          .atop(logMessage)
          .join(execute.transform(() => dispatch))
      )
  }

}
