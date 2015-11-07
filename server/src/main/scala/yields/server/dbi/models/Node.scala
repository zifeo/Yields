package yields.server.dbi.models

import java.time.OffsetDateTime

import yields.server.dbi._
import yields.server.utils.Helpers

/**
 *
 * nodes:nid Long
 * nodes:[nid] Map[attributes -> value]   containing date_creation, last_activity, name, kind
 * nodes:[nid]:users Zset[UID]
 * nodes:[nid]:nodes Zset[NID]
 * nodes:[nid]:feed Zset[tid -> (uid, text, nid, datetime)]
 * nodes:[nid]:content Binary
 * nodes:nid -> last used nid
 */

/**
 * Representation of a node
 */
abstract class Node(val nid: NID) {

  object Key {
    val infos = s"nodes:$nid"
    val date_creation = "creation"
    val last_activity = "activity"
    val updated_at = "updated_at"
    val name = "name"
    val kind = "kind"
    val users = s"$infos:users"
    val nodes = s"$infos:nodes"
    val feed = s"$infos:feed"
    val tid = s"$infos:tid"
  }

  type feedEntry = Map[TID, feedContent]

  var _date_creation: Option[OffsetDateTime] = None
  var _last_activity: Option[OffsetDateTime] = None
  var _name: Option[String] = None
  var _kind: Option[String] = None

  var _users: Option[List[UID]] = None
  var _nodes: Option[List[NID]] = None
  var _feed: Option[List[feedEntry]] = None


  /** dateCreation getter */
  def date_creation: OffsetDateTime = _date_creation.getOrElse {
    _date_creation = redis.hget(Key.infos, Key.date_creation).map(OffsetDateTime.parse)
    valueOrException(_date_creation)
  }

  /** lastActivity getter */
  def last_activity: OffsetDateTime = _last_activity.getOrElse {
    _last_activity = redis.hget(Key.infos, Key.last_activity).map(OffsetDateTime.parse)
    valueOrException(_last_activity)
  }

  /** name getter */
  def name: String = _name.getOrElse {
    _name = redis.hget(Key.infos, Key.name)
    valueOrException(_name)
  }

  /** name setter */
  def name_(n: String): Unit =
  _name = update(Key.name, n)

  /** kind getter */
  def kind: String = _kind.getOrElse{
    _kind = redis.hget(Key.infos, Key.kind)
    valueOrException(_kind)
  }

  /** users getter */
  def users: List[UID] = _users.getOrElse {
    _users = redis.zrange[UID](Key.users, 0, -1)
    valueOrDefault(_users, List())
  }

  /** add user */
  def addUser(id: UID): Boolean =
    hasChangeOneEntry(redis.zadd(Key.users, Helpers.currentDatetime.toEpochSecond, id))

  /** get n messages from an index */
  def getMessagesInRange(start: Int, n: Int): List[feedEntry] = {
    _feed = redis.zrange[feedEntry](Key.feed, start, n)
    valueOrDefault(_feed, List())
  }

  /** add message */
  def addMessage(content: feedContent): Boolean = {
    val tid: TID = redis.incr(Key.tid).getOrElse(0)
    val entry: feedEntry = Map(tid -> content)
    hasChangeOneEntry(redis.zadd(Key.feed, Helpers.currentDatetime.toEpochSecond, entry))
  }

  private def valueOrDefault[T](value: Option[T], default: T): T =
    value.getOrElse(default)

  private def update[T](field: String, value: T): Option[T] = {
    val status = redis.hmset(Key.infos, List((field, value), (Key.updated_at, Helpers.currentDatetime)))
    if (! status) throw new RedisNotAvailableException
    Some(value)
  }

  // Returns whether the count is one or throws an exception on error.
  private def hasChangeOneEntry(count: Option[Long]): Boolean =
    1 == count.getOrElse(throw new RedisNotAvailableException)

  // Gets the value if set or else throws an exception (cannot be unset).
  private def valueOrException[T](value: Option[T]): T =
    value.getOrElse(throw new ModelValueNotSetException)
}