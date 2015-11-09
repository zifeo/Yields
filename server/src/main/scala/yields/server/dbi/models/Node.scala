package yields.server.dbi.models

import java.time.OffsetDateTime

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.{IllegalValueException, UnincrementableIdentifierException}
import yields.server.utils.Temporal
import yields.server.dbi.models._

/**
 * Model of a node with link to the database
 *
 * Node is abstract superclass of every possible kind of nodes like Group, Image etc
 *
 * Database structure :
 * nodes:nid Long - last node id created
 * nodes:[nid] Map[attributes -> value] - name, kind, refreshed_at, created_at, updated_at
 * nodes:[nid]:users Zset[UID] with score datetime
 * nodes:[nid]:nodes Zset[NID] with score datetime
 * nodes:[nid]:tid Long - last time id created
 * nodes:[nid]:feed Zset[(uid, text, nid, datetime)] with score incremental (tid)
 */
abstract class Node {

  object NodeKey {
    val node = s"nodes:$nid"
    val name = "name"
    val kind = "kind"
    val refreshed_at = "refreshed_at"
    val created_at = "created_at"
    val updated_at = "updated_at"
    val users = s"$node:users"
    val nodes = s"$node:nodes"
    val feed = s"$node:feed"
    val tid = s"$node:tid"
  }

  val nid: NID

  private var _name: Option[String] = None
  private var _kind: Option[String] = None
  private var _created_at: Option[OffsetDateTime] = None
  private var _updated_at: Option[OffsetDateTime] = None
  private var _refreshed_at: Option[OffsetDateTime] = None
  private var _users: Option[List[UID]] = None
  private var _nodes: Option[List[NID]] = None
  private var _feed: Option[List[FeedContent]] = None

  /** Name getter. */
  def name: String = _name.getOrElse {
    _name = redis.withClient(_.hget[String](NodeKey.node, NodeKey.name))
    valueOrDefault(_name, "")
  }

  /** Name setter. */
  def name_=(n: String): Unit =
    _name = update(NodeKey.name, n)

  /** Kind getter. */
  def kind: String = _kind.getOrElse {
    _kind = redis.withClient(_.hget[String](NodeKey.node, NodeKey.kind))
    valueOrException(_kind)
  }

  def kind_=(newKind: String): Unit = {
    _kind = update(NodeKey.kind, newKind)
  }

  /** Creation datetime getter. */
  def created_at: OffsetDateTime = _created_at.getOrElse {
    _created_at = redis.withClient(_.hget[OffsetDateTime](NodeKey.node, NodeKey.created_at))
    valueOrException(_created_at)
  }

  /** Update datetime getter. */
  def updated_at: OffsetDateTime = _updated_at.getOrElse {
    _updated_at = redis.withClient(_.hget[OffsetDateTime](NodeKey.node, NodeKey.updated_at))
    valueOrException(_updated_at)
  }

  /** Refresh datetime getter. */
  def refreshed_at: OffsetDateTime = _refreshed_at.getOrElse {
    _refreshed_at = redis.withClient(_.hget[OffsetDateTime](NodeKey.node, NodeKey.refreshed_at))
    valueOrDefault(_refreshed_at, Temporal.notYet)
  }

  /** Users getter */
  def users: List[UID] = _users.getOrElse {
    _users = redis.withClient(_.zrange[UID](NodeKey.users, 0, -1))
    valueOrDefault(_users, List.empty)
  }

  /** Add user */
  def addUser(id: UID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zadd(NodeKey.users, Temporal.current.toEpochSecond, id)))

  /** Remove user */
  def removeUser(id: UID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zrem(NodeKey.users, id)))

  /** Nodes getter */
  def nodes: List[NID] = _nodes.getOrElse {
    _nodes = redis.withClient(_.zrange[NID](NodeKey.nodes, 0, -1))
    valueOrDefault(_nodes, List.empty)
  }

  /** Add node */
  def addNode(nid: NID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zadd(NodeKey.nodes, Temporal.current.toEpochSecond, nid)))

  /** Get n messages starting from some point */
  // TODO: Check that TID (expanding Long) is a valid int
  def getMessagesInRange(start: TID, n: Int): List[FeedContent] = {
    _feed = redis.withClient(_.zrange[FeedContent](NodeKey.feed, start.toInt, n))
    valueOrException(_feed)
  }

  /** Add message */
  def addMessage(content: FeedContent): Boolean = {
    val tid = redis.withClient(_.incr(NodeKey.tid)
      .getOrElse(throw new UnincrementableIdentifierException(s"time identifier (tid) from node $nid fails")))
    hasChangeOneEntry(redis.withClient(_.zadd(NodeKey.feed, tid, content)))
  }

  /** Fill the model with the database content */
  def hydrate(): Unit = {
    val values = redis.withClient(_.hgetall[String, String](NodeKey.node))
      .getOrElse(throw new IllegalValueException("user should have some data"))
    _name = values.get(NodeKey.name)
    _kind = values.get(NodeKey.kind)
    _created_at = values.get(NodeKey.created_at).map(OffsetDateTime.parse)
    _updated_at = values.get(NodeKey.updated_at).map(OffsetDateTime.parse)
    _refreshed_at = values.get(NodeKey.refreshed_at).map(OffsetDateTime.parse)
  }

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (NodeKey.updated_at, Temporal.current))
    redis.withClient(_.hmset(NodeKey.node, updates))
    Some(value)
  }

}

/** [[Node]] companion. */
object Node {

  object StaticKey {
    val nid = "nodes:nid"
  }

  /** Creates a new node by reserving a node identifier. */
  def newNID(): NID =
    redis.withClient(_.incr(StaticKey.nid)
      .getOrElse(throw new UnincrementableIdentifierException("new node identifier (nid) fails")))

}