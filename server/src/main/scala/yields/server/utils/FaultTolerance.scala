package yields.server.utils

import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.event.LoggingAdapter

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.control.NonFatal

/** Regroups custom fault tolerance strategies. */
object FaultTolerance {

  /** Fails only if fatal or occurred > 3 in 1 minutes. */
  def nonFatalResume(log: LoggingAdapter) = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case NonFatal(nonfatal) =>
      val message = nonfatal.getMessage
      log.error(nonfatal, s"non fatal: $message")
      Resume
    case fatal =>
      val message = fatal.getMessage
      log.error(fatal, s"fatal: $message")
      Escalate
  }

}
