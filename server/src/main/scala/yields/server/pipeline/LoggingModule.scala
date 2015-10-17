package yields.server.pipeline

import akka.http.scaladsl.model.DateTime
import akka.util.ByteString

/**
 * Logs every incoming and outgoing items.
 * @tparam C incoming
 * @tparam G outgoing
 */
class LoggingModule[C, G] extends Module[C, C, G, G] {

  /**
   * Logs to stdOut by prefixing item with its chan.
   * @param channel output prefix
   * @tparam T item
   * @return no input change
   */
  def log[T](channel: String): T => T = { item: T =>
    val time = DateTime.now
    val trace = beautifier(item)
    println(f"$time%s $channel%-6s $trace%s")
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

  /** Incoming log with given channel. */
  override val incoming: C => C = log("IN ")

  /** Outgoing log with given channel. */
  override val outgoing: G => G = log("OUT")

}

/** [[LoggingModule]] companion object. */
object LoggingModule {

  /** Shortcut for creating a new logging module. */
  def apply[C, G] = new LoggingModule[C, G].create

}