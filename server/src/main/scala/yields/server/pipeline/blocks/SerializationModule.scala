package yields.server.pipeline.blocks

import akka.event.LoggingAdapter
import akka.util.ByteString
import spray.json._
import yields.server.io._
import yields.server.mpi.{Request, Response}

/**
 * Parse every incoming and outgoing items from and to actions.
 */
class SerializationModule(logger: LoggingAdapter) extends Module[ByteString, Request, Response, ByteString] {

  /** Incoming log with given channel. */
  override val incoming = { raw: ByteString =>
    val json = raw.utf8String.parseJson
    json.convertTo[Request]
  }

  /** Outgoing log with given channel. */
  override val outgoing = { result: Response =>
    val json = result.toJson.toString()
    ByteString(s"$json\n")
  }

}

/** [[SerializationModule]] companion object. */
object SerializationModule {

  /** Shortcut for creating a new logging module. */
  def apply()(implicit logger: LoggingAdapter) = new SerializationModule(logger).create

}