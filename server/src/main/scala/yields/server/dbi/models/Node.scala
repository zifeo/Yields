package yields.server.dbi.models

import java.time.OffsetDateTime

import com.redis.RedisClient.DESC
import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.{IllegalValueException, UnincrementableIdentifierException}
import yields.server.utils.Temporal

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
    val creator = "creator"
    val users = s"$node:users"
    val nodes = s"$node:nodes"
    val feed = s"$node:feed"
  }

  val nid: NID
  private var _name: Option[String] = None
  private var _kind: Option[String] = None
  private var _created_at: Option[OffsetDateTime] = None
  private var _updated_at: Option[OffsetDateTime] = None
  private var _refreshed_at: Option[OffsetDateTime] = None
  private var _creator: Option[UID] = None
  private var _users: Option[List[UID]] = None
  private var _nodes: Option[List[NID]] = None
  private var _feed: Option[List[IncomingFeedContent]] = None

  /** Name getter. */
  def name: String = _name.getOrElse {
    _name = redis.withClient(_.hget[String](NodeKey.node, NodeKey.name))
    valueOrDefault(_name, "")
  }

  /** Name setter. */
  def name_=(n: String): Unit =
    _name = update(NodeKey.name, n)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (NodeKey.updated_at, Temporal.current))
    redis.withClient(_.hmset(NodeKey.node, updates))
    Some(value)
  }

  /** Kind getter. */
  def kind: String = _kind.getOrElse {
    _kind = redis.withClient(_.hget[String](NodeKey.node, NodeKey.kind))
    valueOrException(_kind)
  }

  /** kind setter */
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
    valueOrDefault(_refreshed_at, Temporal.minimum)
  }

  /** creator getter */
  def creator: UID = {
    _creator = redis.withClient(_.hget[UID](NodeKey.node, NodeKey.creator))
    valueOrDefault(_creator, 0)
  }

  /** creator setter */
  def creator_=(uid: UID): Unit = {
    _creator = update(NodeKey.creator, uid)
  }

  /** Users getter */
  def users: List[UID] = _users.getOrElse {
    _users = redis.withClient(_.zrange[UID](NodeKey.users, 0, -1))
    valueOrDefault(_users, List.empty)
  }

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

  /** Remove node */
  def removeNode(nid: NID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zrem(NodeKey.nodes, Temporal.current.toEpochSecond, nid)))

  /** Get n messages starting from some point */
  def getMessagesInRange(datetime: OffsetDateTime, count: Int): List[IncomingFeedContent] = {
    _feed = redis.withClient(_.zrangebyscore[IncomingFeedContent](
      NodeKey.feed,
      min = Temporal.minimum.toEpochSecond,
      max = datetime.toEpochSecond,
      limit = Some((0, count)),
      sortAs = DESC
    ))
    //TODO: Fix key
    valueOrException(_feed)
  }

  /** Add message */
  def addMessage(content: IncomingFeedContent): Boolean = {
    hasChangeOneEntry(redis.withClient(_.zadd(NodeKey.feed, content._1.toEpochSecond, content)))
  }

  /** Add multiple users to the group */
  def addMultipleUser(users: Seq[UID]): Unit =
    users.foreach(addUser)

  /** Add user */
  def addUser(id: UID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zadd(NodeKey.users, Temporal.current.toEpochSecond, id)))

  /** Add multiple nodes to the group */
  def addMultipleNodes(nodes: Seq[NID]): Unit =
    nodes.foreach(addNode)

  /** Remove multiple nodes */
  def remMultipleNodes(nodes: Seq[NID]): Unit =
    nodes.foreach(removeNode)

  /** Add node */
  def addNode(nid: NID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zadd(NodeKey.nodes, Temporal.current.toEpochSecond, nid)))

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
  }

}

/** [[Node]] companion. */
object Node {

  /** Creates a new node by reserving a node identifier. */
  def newNID(): NID =
    redis.withClient(_.incr(StaticKey.nid)
      .getOrElse(throw new UnincrementableIdentifierException("new node identifier (nid) fails")))

  object StaticKey {
    val nid = "nodes:nid"
  }

}
