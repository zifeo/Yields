package yields.server.pipeline.blocks

import akka.event.LoggingAdapter
import akka.stream.scaladsl.BidiFlow
import akka.util.ByteString
import spray.json._
import yields.server.actions.Broadcast
import yields.server.io._
import yields.server.mpi.{Notification, Request, Response}

/**
  * Parse every incoming and outgoing items from and to actions.
  */
class SerializationModule(logger: LoggingAdapter) extends Module[ByteString, Request, Response, ByteString] {

  import SerializationModule._

  /** Incoming deserialization. */
  override val incoming: ByteString => Request = deserialize[Request]

  /** Outgoing serialization. */
  override val outgoing: Response => ByteString = serialize

}

/** [[SerializationModule]] companion object. */
object SerializationModule {

  /** Serialize [[Response]] to [[ByteString]]. '\n' is needed by client to detect end of response. */
  def serialize(response: Response): ByteString = {
    val json = response.toJson
    ByteString(s"$json\n")
  }

  /** Serialize [[Notification]] to [[ByteString]]. '\n' is needed by client to detect end of response. */
  def serialize(notification: Notification): ByteString = {
    val json = notification.toJson
    ByteString(s"$json\n")
  }

  /** Deserialize [[ByteString]] to [[Request]]. */
  def deserialize[T : JsonReader](raw: ByteString): T =
    raw.utf8String.parseJson.convertTo[T]

  /** Shortcut for creating a new logging module. */
  def apply()(implicit logger: LoggingAdapter): BidiFlow[ByteString, Request, Response, ByteString, Unit] =
    new SerializationModule(logger).create

}