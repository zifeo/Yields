package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal

/**
  * Rss publisher node class
  * @param nid nid of publisher
  */
final class Rss private(nid: NID) extends Node(nid) with Tags {

  val nodeKey = NodeKey.node
  override val nodeID = nid

  object RSSKey {
    val url = "rss_url"
  }

  /** Add message */
  override def addMessage(content: FeedContent): Boolean = {
    val children = for (nid <- nodes) yield {
      Node(nid).addMessage(content)
    }
    children.forall(x => x)
  }

  private var _url: Option[String] = None

  /** url getter. */
  def url: String = _url.getOrElse {
    _url = redis(_.hget[String](NodeKey.node, RSSKey.url))
    valueOrException(_url)
  }

  /** url setter. */
  def url_=(u: String): Unit =
    _url = update(StaticNodeKey.name, u)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (StaticNodeKey.updated_at, Temporal.now))
    redis(_.hmset(NodeKey.node, updates))
    Some(value)
  }

}

/** Companion object for [[Rss]] */
object Rss {

  object StaticRssKey {
    val filter = "filter"
  }

  /**
    * Create a new rss in db
    * @param name rss name to create
    * @param url rss url
    * @param filter terms filtering the RSS
    * @return rss object
    */
  def create(name: String, url: String, filter: String): Rss = {
    val rss = Rss(newIdentity())
    val now = Temporal.now
    val infos = List(
      (StaticNodeKey.name, name),
      (StaticRssKey.filter, filter),
      (StaticNodeKey.kind, classOf[Group].getSimpleName),
      (StaticNodeKey.created_at, now),
      (StaticNodeKey.refreshed_at, now),
      (StaticNodeKey.updated_at, now)
    )
    assert(redis(_.hmset(rss.NodeKey.node, infos)))
    rss
  }

  /** Builds a RSS node. */
  def apply(nid: NID) = {
    new Rss(nid)
  }

}