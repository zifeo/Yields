package yields.server.pipeline.blocks

import akka.event.LoggingAdapter
import akka.stream.scaladsl.BidiFlow
import akka.util.ByteString

/**
 * Logs every incoming and outgoing items.
 * @tparam C incoming
 * @tparam G outgoing
 */
class LoggerModule[C, G](logger: LoggingAdapter) extends Module[C, C, G, G] {

  /**
   * Logs to stdOut by prefixing item with its chan.
   * @param channel output prefix
   * @tparam T item
   * @return no input change
   */
  def log[T](channel: String): T => T = { item: T =>
    val trace = beautifier(item)
    logger.info(s"[$channel] $trace")
    item
  }

  /**
   * Performs beautifying to allow log to be more readable.
   * @param item element to beautify
   * @tparam T item
   * @return pretty string representation
   */
  def beautifier[T](item: T): String = item match {
    case byteStr: ByteString => byteStr.utf8String
    case _ => item.toString
  }

  /** Logs incoming item. */
  override val incoming: C => C = log("IN")

  /** Logs outgoing item. */
  override val outgoing: G => G = log("OUT")

}

/** [[LoggerModule]] companion object. */
object LoggerModule {

  /** Shortcut for creating a new logging module. */
  def apply[C, G]()(implicit logger: LoggingAdapter): BidiFlow[C, C, G, G, Unit] =
    new LoggerModule[C, G](logger).create

}