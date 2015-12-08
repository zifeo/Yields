package yields.server.rss

import akka.actor._
import yields.server.utils.FaultTolerance

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

/**
  * RSS pooler starts pooling RSS at start and periodically continue after.
  */
final class RSSPooler extends Actor with ActorLogging {

  import RSSPooler._

  def receive: Receive = {

    // ----- RSSPooler -----

    case Pool =>
      log.info("new RSS pooling round")

      context.system.scheduler.scheduleOnce(1 seconds, self, Pool)

    // ----- Default -----

    case unexpected =>
      log.warning(s"unexpected letter: unexpected")

  }

  override val supervisorStrategy =
    FaultTolerance.nonFatalResumeOrEscalate(log)

  override def preStart(): Unit = {
    self ! Pool
  }

}

/** [[RSSPooler]] companion object. */
object RSSPooler {

  /** Restart pooling. */
  private case object Pool

  /** Creates a RSS pooler props. */
  def props: Props =
    Props(classOf[RSSPooler])

}
