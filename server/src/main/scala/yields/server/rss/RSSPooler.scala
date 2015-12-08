package yields.server.rss

import akka.actor._
import yields.server.dbi.models.RSS
import yields.server.utils.{Temporal, FaultTolerance}

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
      log.info("RSS pooling: round starts")
      RSS.all.foreach { rss =>

        val feed = new RSSFeed(rss.url)
        val news = feed.sinceFiltered(rss.refreshed_at, rss.filter)
        for ((date, title, author, link) <- news) {
          rss.addMessage((Temporal.now, rss.nid, None, s"$title $link"))
        }

        if (news.nonEmpty) {
          rss.updated()
          val name = rss.name
          val newsCount = news.size
          log.debug(s"RSS poolin: $name refreshed with $newsCount")
        }
        rss.refreshed()

      }
      context.system.scheduler.scheduleOnce(5 seconds, self, Pool)
      log.info("RSS pooling: round ends")

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
