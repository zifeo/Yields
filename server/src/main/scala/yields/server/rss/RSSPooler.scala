package yields.server.rss

import akka.actor._
import yields.server.Yields
import yields.server.actions.nodes.NodeMessageBrd
import yields.server.dbi.models.{Media, Node, RSS}
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
      val updates = updateRSS(RSS.all)
      updates.foreach { case (rssName, entryTitle) =>
        log.debug(s"RSS pooling: $rssName refreshed with $entryTitle")
      }
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

  /**
    * Goes through all RSS, gets updates, spreads news and broadcasts it.
    * @return list of RSS name with new title entry.
    */
  def updateRSS(rss: List[RSS]): List[(String, String)] =
    rss.flatMap { rss =>
      val feed = new RSSFeed(rss.url)
      val news = feed.sinceFiltered(rss.refreshedAt, rss.filter)
      if (news.nonEmpty) {
        rss.refreshed()
      }

      news.map { case RSSEntry(title, author, link, entry, _) =>
        val now = Temporal.now
        val media = Media.create("url", link, rss.nid)
        val text = s"$title $link"
        rss.addMessage((now, rss.nid, Some(media.nid), text))
        rss.receivers.map(Node(_)).foreach { node =>
          Yields.broadcast(node.users) {
            NodeMessageBrd(node.nid, now, rss.nid, Some(text), Some("url"), Some(media.content), Some(media.nid))
          }
        }
        (rss.name, title)
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
