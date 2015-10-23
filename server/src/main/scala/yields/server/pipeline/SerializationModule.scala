package yields.server.pipeline

import akka.event.LoggingAdapter
import akka.util.ByteString
import spray.json._
import yields.server.io._
import yields.server.actions.Action

/**
 * Parse every incoming and outgoing items from and to Yields message.
 */
class ParserModule(logger: LoggingAdapter) extends Module[ByteString, Action, Action, ByteString] {

  /** Incoming log with given channel. */
  override val incoming = { raw: ByteString =>
    val json = raw.utf8String.parseJson
    json.convertTo[Action]
  }

  /** Outgoing log with given channel. */
  override val outgoing = { message: Action =>
    val json = message.toJson
    ByteString(json.toString())
  }

}

/** [[ParserModule]] companion object. */
object ParserModule {

  /** Shortcut for creating a new logging module. */
  def apply()(implicit logger: LoggingAdapter) = new ParserModule(logger).create

}