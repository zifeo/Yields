package yields.server

import akka.event.LoggingAdapter

import scala.collection.mutable

/**
  * Fakes a logger and allows access to all logs.
  * The log are write-wise thread-safe but the read should be done sequentially.
  * For testing-purpose only as not fine-grained.
  */
final class FakeLogger extends LoggingAdapter {

  val debug = mutable.ListBuffer.empty[String]
  val info = mutable.ListBuffer.empty[String]
  val warning = mutable.ListBuffer.empty[String]
  val error = mutable.ListBuffer.empty[String]

  override def isDebugEnabled: Boolean = true
  override def isInfoEnabled: Boolean = true
  override def isWarningEnabled: Boolean = true
  override def isErrorEnabled: Boolean = true

  override protected def notifyDebug(message: String): Unit = debug.synchronized(debug += message)
  override protected def notifyInfo(message: String): Unit = info.synchronized(info += message)
  override protected def notifyWarning(message: String): Unit = warning.synchronized(warning += message)
  override protected def notifyError(message: String): Unit = error.synchronized(error += message)
  override protected def notifyError(cause: Throwable, message: String): Unit = error.synchronized(error += message)

  /** Trashes all logs. */
  def clear(): Unit = {
    debug.clear()
    info.clear()
    warning.clear()
    error.clear()
  }

}