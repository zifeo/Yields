package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.dbi.models.RSS.StaticRssKey
import yields.server.utils.Temporal

/**
  * Rss publisher node class
  * @param nid nid of publisher
  */
final class RSS private(nid: NID) extends Node(nid) with Tags {

  val nodeKey = NodeKey.node
  override val nodeID = nid

  private var _url: Option[String] = None
  private var _filter: Option[String] = None

  /** Name setter. */
  override def name_=(n: String): Unit = {
    Indexes.searchableUnregister(name, nid)
    super.name_=(n)
    Indexes.searchableRegister(name, nid)
  }

  /** Add message */
  override def addMessage(content: FeedContent): Boolean = {
    val children = for (nid <- receivers) yield {
      Node(nid).addMessage(content)
    }
    children.forall(x => x)
  }

  /** Filter getter. */
  def filter: String = _filter.getOrElse {
    _filter = redis(_.hget[String](NodeKey.node, StaticRssKey.filter))
    valueOrException(_filter)
  }

  /** Url getter. */
  def url: String = _url.getOrElse {
    _url = redis(_.hget[String](NodeKey.node, StaticRssKey.url))
    valueOrException(_url)
  }

}

/** Companion object for [[RSS]] */
object RSS {

  object StaticRssKey {
    val url = "url"
    val filter = "filter"
  }

  /**
    * Create a new rss in db
    * @param name rss name to create
    * @param url rss url
    * @param filter terms filtering the RSS
    * @return rss object
    */
  def create(name: String, url: String, filter: String): RSS = {
    val rss = RSS(newIdentity())
    val now = Temporal.now
    val infos = List(
      (StaticNodeKey.name, name),
      (StaticRssKey.url, url),
      (StaticRssKey.filter, filter),
      (StaticNodeKey.kind, classOf[RSS].getSimpleName),
      (StaticNodeKey.created_at, now),
      (StaticNodeKey.refreshed_at, now),
      (StaticNodeKey.updated_at, now)
    )
    assert(redis(_.hmset(rss.NodeKey.node, infos)))
    assert(Indexes.rssRegister(rss.nid))
    assert(Indexes.searchableRegister(name, rss.nid))
    rss
  }

  /** Returns all rss. */
  def all: List[RSS] =
   Indexes.rssLookup.map(RSS(_))

  /** Builds a RSS node. */
  def apply(nid: NID) = {
    new RSS(nid)
  }

}