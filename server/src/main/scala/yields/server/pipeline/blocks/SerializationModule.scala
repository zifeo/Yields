package yields.server.pipeline.blocks

import akka.event.LoggingAdapter
import akka.stream.scaladsl.BidiFlow
import akka.util.ByteString
import spray.json._
import yields.server.io._
import yields.server.mpi.{Request, Response}

/**
  * Parse every incoming and outgoing items from and to actions.
  */
class SerializationModule(logger: LoggingAdapter) extends Module[ByteString, Request, Response, ByteString] {

  import SerializationModule._

  /** Incoming log with given channel. */
  override val incoming: ByteString => Request = deserialize

  /** Outgoing log with given channel. '\n' is needed by client to detect end of response. */
  override val outgoing: Response => ByteString = serialize

}

/** [[SerializationModule]] companion object. */
object SerializationModule {

  /** Serialize to [[ByteString]]. */
  def serialize(response: Response): ByteString = {
    val json = response.toJson.toString()
    ByteString(s"$json\n")
  }

  /** Deserialize to [[Request]]. */
  def deserialize(raw: ByteString): Request =
    raw.utf8String.parseJson.convertTo[Request]

  /** Shortcut for creating a new logging module. */
  def apply()(implicit logger: LoggingAdapter): BidiFlow[ByteString, Request, Response, ByteString, Unit] =
    new SerializationModule(logger).create

}