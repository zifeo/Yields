package yields.server.rss

import akka.actor._
import yields.server.Yields
import yields.server.actions.nodes.NodeMessageBrd
import yields.server.dbi.models.{Node, RSS}
import yields.server.utils.{Config, FaultTolerance, Temporal}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * RSS pooler starts pooling RSS at start and periodically continue after.
  */
final class RSSPooler extends Actor with ActorLogging {

  import RSSPooler._

  val rsscooling = Config.getInt("rsscooling")

  def receive: Receive = {

    // ----- RSSPooler -----

    case Pool =>
      log.info("RSS pooling: round starts")
      updateAllRSS()
      context.system.scheduler.scheduleOnce(rsscooling.seconds, self, Pool)
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

  /** Goes through all RSS, gets updates, spreads the news and broadcasts it. */
  def updateAllRSS(): Unit =
    RSS.all.foreach { rss =>
      val feed = new RSSFeed(rss.url)
      val news = feed.sinceFiltered(rss.refreshed_at, rss.filter)
      val name = rss.name

      for (RSSEntry(title, author, link, entry, _) <- news) {
        val now = Temporal.now
        rss.addMessage((now, rss.nid, None, s"$title $link"))

        rss.receivers.map(Node(_)).foreach { node =>
          Yields.broadcast(node.receivers) {
            NodeMessageBrd(node.nid, now, rss.nid, Some(title), None, None, None)
          }
        }

        log.debug(s"RSS pooling: $name refreshed with $title")
      }

      if (news.nonEmpty) {
        rss.refreshed()
      }
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
