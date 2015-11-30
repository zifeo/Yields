package yields.server.dbi.models

import java.time.OffsetDateTime

import com.redis.RedisClient.DESC
import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.IllegalValueException
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal

/**
  * Model of a node with link to the database
  *
  * Node is abstract superclass of every possible kind of nodes like Group, Image etc
  *
  * Database structure :
  * nodes:[nid] Map[String, String] - name, kind, refreshed_at, created_at, updated_at
  * nodes:[nid]:users Map[UID, OffsetDateTIme] with score datetime
  * nodes:[nid]:nodes Map[NID, OffsetDateTIme] with score datetime
  * nodes:[nid]:feed Zset[(uid, text, nid, datetime)] with score incremental (tid)
  */
class Node protected(val nid: NID) {

  object NodeKey {
    val node = s"nodes:$nid"
    val users = s"$node:users"
    val nodes = s"$node:nodes"
    val feed = s"$node:feed"
  }

  private var _name: Option[String] = None
  private var _kind: Option[String] = None
  private var _created_at: Option[OffsetDateTime] = None
  private var _updated_at: Option[OffsetDateTime] = None
  private var _refreshed_at: Option[OffsetDateTime] = None
  private var _creator: Option[UID] = None
  private var _users: Option[List[UID]] = None
  private var _nodes: Option[List[NID]] = None
  private var _feed: Option[List[FeedContent]] = None
  private var _pic: Option[NID] = None

  /** Name getter. */
  def name: String = _name.getOrElse {
    _name = redis(_.hget[String](NodeKey.node, StaticNodeKey.name))
    valueOrDefault(_name, "")
  }

  /** Name setter. */
  def name_=(n: String): Unit =
    _name = update(StaticNodeKey.name, n)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (StaticNodeKey.updated_at, Temporal.now))
    redis(_.hmset(NodeKey.node, updates))
    Some(value)
  }

  /** Kind getter. */
  def kind: String = _kind.getOrElse {
    _kind = redis(_.hget[String](NodeKey.node, StaticNodeKey.kind))
    valueOrException(_kind)
  }

  /** kind setter */
  def kind_=(newKind: String): Unit = {
    _kind = update(StaticNodeKey.kind, newKind)
  }

  /** Creation datetime getter. */
  def created_at: OffsetDateTime = _created_at.getOrElse {
    _created_at = redis(_.hget[OffsetDateTime](NodeKey.node, StaticNodeKey.created_at))
    valueOrException(_created_at)
  }

  /** Update datetime getter. */
  def updated_at: OffsetDateTime = _updated_at.getOrElse {
    _updated_at = redis(_.hget[OffsetDateTime](NodeKey.node, StaticNodeKey.updated_at))
    valueOrException(_updated_at)
  }

  /** Refresh datetime getter. */
  def refreshed_at: OffsetDateTime = _refreshed_at.getOrElse {
    _refreshed_at = redis(_.hget[OffsetDateTime](NodeKey.node, StaticNodeKey.refreshed_at))
    valueOrDefault(_refreshed_at, Temporal.minimum)
  }

  /** creator getter */
  def creator: UID = {
    _creator = redis(_.hget[UID](NodeKey.node, StaticNodeKey.creator))
    valueOrDefault(_creator, 0)
  }

  /** creator setter */
  def creator_=(uid: UID): Unit = {
    _creator = update(StaticNodeKey.creator, uid)
  }

  /** Users getter */
  def users: List[UID] = _users.getOrElse {
    _users = redis(_.hkeys[UID](NodeKey.users))
    valueOrDefault(_users, List.empty)
  }

  /** Add user. */
  def addUser(newUser: UID): Boolean =
    addWithTime(NodeKey.users, newUser)

  /** Add multiple users. */
  def addUser(newUsers: List[UID]): Boolean =
    addWithTime(NodeKey.users, newUsers)

  /** Remove user. */
  def removeUser(oldUser: UID): Boolean =
    remWithTime(NodeKey.users, oldUser)

  /** Remove multiple users. */
  def removeUser(oldUsers: List[UID]): Boolean =
    remWithTime(NodeKey.users, oldUsers)

  /** Nodes getter. */
  def nodes: List[NID] = _nodes.getOrElse {
    _nodes = redis(_.hkeys[NID](NodeKey.nodes))
    valueOrDefault(_nodes, List.empty)
  }

  /** Add node. */
  def addNode(newNode: NID): Boolean =
    addWithTime(NodeKey.nodes, newNode)

  /** Add multiple nodes. */
  def addNode(newNodes: List[NID]): Boolean =
    addWithTime(NodeKey.nodes, newNodes)

  /** Remove node. */
  def removeNode(oldNode: NID): Boolean =
    remWithTime(NodeKey.nodes, oldNode)

  /** Remove multiple nodes. */
  def removeNode(oldNode: List[NID]): Boolean =
    remWithTime(NodeKey.nodes, oldNode)

  /** Picture getter. */
  def pic: Blob = {
    _pic = redis(_.hget[NID](NodeKey.node, StaticNodeKey.node_pic))
    if (_pic.isDefined) {
      val m = Media(_pic.get)
      m.content
    } else {
      Array.empty
    }
  }

  /**
    * Picture setter.
    * Delete old picture if there is one and create new media on disk
    */
  def picSetter(content: Blob, creator: UID): Unit = {
    if (_pic.isDefined) {
      Media.deleteContentOnDisk(_pic.get)
    }
    val newPic = Media.create("image", content, creator)
    _pic = update(StaticNodeKey.node_pic, newPic.nid)
  }

  /** Get n messages starting from some point */
  def getMessagesInRange(datetime: OffsetDateTime, count: Int): List[FeedContent] = {
    _feed = redis(_.zrangebyscore[FeedContent](
      NodeKey.feed,
      min = Temporal.minimum.toEpochSecond,
      max = datetime.toEpochSecond,
      limit = Some((0, count)),
      sortAs = DESC
    )).map(_.reverse)
    valueOrException(_feed)
  }

  /** Add message */
  def addMessage(content: FeedContent): Boolean = {
    valueOrException(redis(_.zadd(NodeKey.feed, content._1.toEpochSecond, content))) == 1
  }

  /** Fill the model with the database content */
  def hydrate(): Unit = {
    val values = redis(_.hgetall[String, String](NodeKey.node))
      .getOrElse(throw new IllegalValueException("user should have some data"))
    _name = values.get(StaticNodeKey.name)
    _kind = values.get(StaticNodeKey.kind)
    _created_at = values.get(StaticNodeKey.created_at).map(OffsetDateTime.parse)
    _updated_at = values.get(StaticNodeKey.updated_at).map(OffsetDateTime.parse)
    _refreshed_at = values.get(StaticNodeKey.refreshed_at).map(OffsetDateTime.parse)
  }

}

/** [[Node]] companion object. */
object Node {

  object StaticNodeKey {
    val name = "name"
    val kind = "kind"
    val refreshed_at = "refreshed_at"
    val created_at = "created_at"
    val updated_at = "updated_at"
    val creator = "creator"
    val node_pic = "pic_nid"
  }

  /** Node constructor. */
  def apply(nid: NID): Node = {
    new Node(nid)
  }

}