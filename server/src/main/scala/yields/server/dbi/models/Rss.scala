package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal

final class Rss private (nid: NID) extends AbstractPublisher(nid) {

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
    _url = update(StaticNodeKey.name, u)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (StaticNodeKey.updated_at, Temporal.now))
    redis.withClient(_.hmset(NodeKey.node, updates))
    Some(value)
  }

}

object Rss {
  def createRss(name: String, url: String): Rss = {
    val rss = Rss(newIdentity())
    redis.withClient { r =>
      val infos = List(
        (StaticNodeKey.created_at, Temporal.now),
        (StaticNodeKey.name, name),
        (StaticNodeKey.kind, classOf[Group].getSimpleName)
      )
      r.hmset(rss.NodeKey.node, infos)
    }
    rss
  }

  def apply(nid: NID) = {
    new Rss(nid)
  }
}