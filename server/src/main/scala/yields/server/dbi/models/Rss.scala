package yields.server.dbi.models

import yields.server.dbi._
import yields.server.utils.Temporal

case class Rss private(override val nid: NID) extends AbstractPublisher {

  object RSSKey {
    val url = "rss_url"
  }

  var _url: Option[String] = None

  /** url getter. */
  def url: String = _url.getOrElse {
    _url = redis.withClient(_.hget[String](NodeKey.node, RSSKey.url))
    valueOrException(_url)
  }

  /** url setter. */
  def url_=(u: String): Unit =
    _url = update(NodeKey.name, u)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (NodeKey.updated_at, Temporal.now))
    redis.withClient(_.hmset(NodeKey.node, updates))
    Some(value)
  }

}

object Rss {
  def createRss(name: String, url: String): Rss = {
    val rss = Rss(Node.newNID())
    redis.withClient { r =>
      import rss.NodeKey
      val infos = List(
        (NodeKey.created_at, Temporal.now),
        (NodeKey.name, name),
        (NodeKey.kind, classOf[Group].getSimpleName)
      )
      r.hmset(rss.NodeKey.node, infos)
    }
    rss
  }

  def apply(nid: NID) = {
    new Rss(nid)
  }
}