package yields.server.dbi.models

import java.time.OffsetDateTime

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.{ModelValueNotSetException, RedisNotAvailableException}
import yields.server.utils.Helpers

/**
 *
 * nodes:nid Long
 * nodes:[nid] Map[attributes -> value]   containing date_creation, last_activity, name, kind
 * nodes:[nid]:users Zset[UID]
 * nodes:[nid]:nodes Zset[NID]
 * nodes:[nid]:feed Zset[tid -> (uid, text, nid, datetime)]
 * nodes:nid -> last used nid
 */

/**
 * Representation of a node
 */
abstract class Node {

  object Key {
    val node = s"nodes:$nid"
    val refreshed_at = "refreshed_at"
    val created_at = "created_at"
    val updated_at = "updated_at"
    val name = "name"
    val kind = "kind"
    val users = s"$node:users"
    val nodes = s"$node:nodes"
    val feed = s"$node:feed"
    val tid = s"$node:tid"
  }

  val nid: NID

  private var _created_at: Option[OffsetDateTime] = None
  private var _refreshed_at: Option[OffsetDateTime] = None
  private var _name: Option[String] = None
  private var _kind: Option[String] = None
  private var _users: Option[List[UID]] = None
  private var _nodes: Option[List[NID]] = None
  private var _feed: Option[List[FeedContent]] = None

  /** Creation datetime getter. */
  def created_at: OffsetDateTime = _created_at.getOrElse {
    _created_at = redis.hget[OffsetDateTime](Key.name, Key.created_at)
    valueOrException(_created_at)
  }

  /** Refresh datetime getter. */
  def refreshed_at: OffsetDateTime = _refreshed_at.getOrElse {
    _refreshed_at = redis.hget[OffsetDateTime](Key.name, Key.refreshed_at)
    valueOrException(_refreshed_at)
  }

  /** Name getter. */
  def name: String = _name.getOrElse {
    _name = redis.hget[String](Key.name, Key.name)
    valueOrException(_name)
  }

  /** Name setter. */
  def name_(n: String): Unit =
    _name = update(Key.name, n)

  /** Kind getter. */
  def kind: String = _kind.getOrElse{
    _kind = redis.hget[String](Key.name, Key.kind)
    valueOrException(_kind)
  }

  /** Users getter. */
  def users: List[UID] = _users.getOrElse {
    _users = redis.zrange[UID](Key.users, 0, -1)
    valueOrDefault(_users, List())
  }

  /** Add user. */
  def addUser(id: UID): Boolean =
    hasChangeOneEntry(redis.zadd(Key.users, Helpers.currentDatetime.toEpochSecond, id))

  /** Get n messages from an index. */
  def getMessagesInRange(start: Int, n: Int): List[FeedContent] = {
    _feed = redis.zrange[FeedContent](Key.feed, start, n)
    valueOrDefault(_feed, List())
  }

  /** Add message. */
  def addMessage(content: FeedContent): Boolean = {
    val tid: TID = redis.incr(Key.tid).getOrElse(0)
    hasChangeOneEntry(redis.zadd(Key.feed, tid, content))
  }

  /** Node getter. */
  def node: List[NID] = _nodes.getOrElse {
    _nodes = redis.zrange[NID](Key.nodes, 0, -1)
    valueOrDefault(_nodes, List())
  }

  /** Add node. */
  def addNode(nid: NID): Boolean =
    hasChangeOneEntry(redis.zadd(Key.nodes, Helpers.currentDatetime.toEpochSecond, nid))

  private def update[T](field: String, value: T): Option[T] = {
    val status = redis.hmset(Key.node, List((field, value), (Key.updated_at, Helpers.currentDatetime)))
    if (! status) throw new RedisNotAvailableException
    Some(value)
  }

  // Returns whether the count is one or throws an exception on error.
  private def hasChangeOneEntry(count: Option[Long]): Boolean =
    1 == count.getOrElse(throw new RedisNotAvailableException)

  // Gets the value if set or else throws an exception (cannot be unset).
  private def valueOrException[T](value: Option[T]): T =
    value.getOrElse(throw new ModelValueNotSetException)

  private def valueOrDefault[T](value: Option[T], default: T): T =
    value.getOrElse(default)

}
