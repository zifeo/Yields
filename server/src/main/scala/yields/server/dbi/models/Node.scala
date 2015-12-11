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
  * nodes:[nid]:users Map[UID, OffsetDateTime] with score datetime
  * nodes:[nid]:nodes Map[NID, OffsetDateTime] with score datetime
  * nodes:[nid]:feed Zset[(uid, text, nid, datetime)] with score incremental (tid)
  */
class Node protected(val nid: NID) {

  object NodeKey {
    val node = s"nodes:$nid"
    val users = s"$node:users"
    val nodes = s"$node:nodes"
    val feed = s"$node:feed"
    val receivers =  s"$node:receivers"
  }

  private var _name: Option[String] = None
  private var _kind: Option[String] = None
  private var _createdAt: Option[OffsetDateTime] = None
  private var _updatedAt: Option[OffsetDateTime] = None
  private var _refreshedAt: Option[OffsetDateTime] = None
  private var _creator: Option[UID] = None
  private var _users: Option[List[UID]] = None
  private var _nodes: Option[List[NID]] = None
  private var _receivers: Option[List[NID]] = None
  private var _feed: Option[List[FeedContent]] = None
  private var _pic: Option[Media] = None

  /** Name getter. */
  def name: String = _name.getOrElse {
    _name = redis(_.hget[String](NodeKey.node, StaticNodeKey.name))
    valueOrDefault(_name, "")
  }

  /** Name setter. */
  def name_=(n: String): Unit =
    _name = update(NodeKey.node, StaticNodeKey.name, n)

  /** Kind getter. */
  def kind: String = _kind.getOrElse {
    _kind = redis(_.hget[String](NodeKey.node, StaticNodeKey.kind))
    valueOrException(_kind)
  }

  /** kind setter */
  def kind_=(newKind: String): Unit = {
    _kind = update(NodeKey.node, StaticNodeKey.kind, newKind)
  }

  /** Creation datetime getter. */
  def createdAt: OffsetDateTime = _createdAt.getOrElse {
    _createdAt = redis(_.hget[OffsetDateTime](NodeKey.node, StaticNodeKey.created_at))
    valueOrException(_createdAt)
  }

  /** Update datetime getter. */
  def updatedAt: OffsetDateTime = _updatedAt.getOrElse {
    _updatedAt = redis(_.hget[OffsetDateTime](NodeKey.node, StaticNodeKey.updated_at))
    valueOrException(_updatedAt)
  }

  /** Updates datetime setter. */
  def updated(): Unit =
    _updatedAt = update(NodeKey.node, StaticNodeKey.updated_at, Temporal.now)

  /** Refresh datetime getter. */
  def refreshedAt: OffsetDateTime = _refreshedAt.getOrElse {
    _refreshedAt = redis(_.hget[OffsetDateTime](NodeKey.node, StaticNodeKey.refreshed_at))
    valueOrDefault(_refreshedAt, Temporal.minimum)
  }

  /** Refresh datetime setter. */
  def refreshedAt_=(dateTime: OffsetDateTime): Unit =
    _refreshedAt = update(NodeKey.node, StaticNodeKey.refreshed_at, dateTime)

  /** Refreshes datetime setter. */
  def refreshed(): Unit =
    refreshedAt = Temporal.now

  /** creator getter */
  def creator: UID = {
    _creator = redis(_.hget[UID](NodeKey.node, StaticNodeKey.creator))
    valueOrDefault(_creator, 0)
  }

  /** creator setter */
  def creator_=(uid: UID): Unit = {
    _creator = update(NodeKey.node, StaticNodeKey.creator, uid)
  }

  /** Users getter */
  def users: List[UID] = _users.getOrElse {
    _users = redis(_.hkeys[UID](NodeKey.users))
    valueOrException(_users)
  }

  /** Add user. */
  def addUser(newUser: UID): Boolean = {
    _users = Some((newUser :: users).distinct)
    addWithTime(NodeKey.users, newUser)
  }

  /** Add multiple users. */
  def addUser(newUsers: List[UID]): Boolean = {
    _users = Some((users ++ newUsers).distinct)
    addWithTime(NodeKey.users, newUsers)
  }

  /** Remove user. */
  def removeUser(oldUser: UID): Boolean = {
    _users = Some(users.filter(_ != oldUser))
    remWithTime(NodeKey.users, oldUser)
  }

  /** Remove multiple users. */
  def removeUser(oldUsers: List[UID]): Boolean = {
    _users = Some(users.diff(oldUsers))
    remWithTime(NodeKey.users, oldUsers)
  }

  /** Receivers getter. */
  def receivers: List[NID] = _nodes.getOrElse {
    _receivers = redis(_.hkeys[NID](NodeKey.receivers))
    valueOrDefault(_receivers, List.empty)
  }

  /** Add receiver. */
  def addReceiver(newReceiver: NID): Boolean = {
    _receivers = Some((newReceiver :: receivers).distinct)
    addWithTime(NodeKey.receivers, newReceiver)
  }

  /** Remove receiver. */
  def removeReceiver(oldReceiver: NID): Boolean = {
    _receivers = Some(receivers.filter(_ != oldReceiver))
    remWithTime(NodeKey.receivers, oldReceiver)
  }

  /** Nodes getter. */
  def nodes: List[NID] = _nodes.getOrElse {
    _nodes = redis(_.hkeys[NID](NodeKey.nodes))
    valueOrDefault(_nodes, List.empty)
  }

  /** Add node. */
  def addNode(newNode: NID): Boolean = {
    _nodes = Some((newNode :: nodes).distinct)
    Node(newNode).addReceiver(nid)
    if (newNode == nid) false
    else addWithTime(NodeKey.nodes, newNode)
  }

  /** Add multiple nodes. */
  def addNode(newNodes: List[NID]): Boolean = {
    _nodes = Some((nodes ++ newNodes).distinct)
    newNodes.foreach(Node(_).addReceiver(nid))
    if (newNodes == List(nid)) false
    else addWithTime(NodeKey.nodes, newNodes.filterNot(_ == nid))
  }

  /** Remove node. */
  def removeNode(oldNode: NID): Boolean = {
    _nodes = Some(nodes.filter(_ != oldNode))
    Node(oldNode).removeReceiver(nid)
    remWithTime(NodeKey.nodes, oldNode)
  }

  /** Remove multiple nodes. */
  def removeNode(oldNode: List[NID]): Boolean = {
    _nodes = Some(nodes.diff(oldNode))
    oldNode.foreach(Node(_).removeReceiver(nid))
    remWithTime(NodeKey.nodes, oldNode)
  }

  /** Picture getter. */
  def pic: Blob = _pic.map(_.content).getOrElse {
    val nid = redis(_.hget[NID](NodeKey.node, StaticNodeKey.node_pic))
    _pic = nid.map(Media(_))
    _pic.map(_.content).getOrElse("")
  }

  /**
    * Picture setter.
    * Delete old picture if there is one and create new media on disk
    */
  def pic(content: Blob, creator: UID): Unit = {
    val newPic = Media.create("image", content, creator)
    val nid = update(NodeKey.node, StaticNodeKey.node_pic, newPic.nid)
    _pic = nid.map(Media(_))
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
    refreshed
    valueOrException(redis(_.zadd(NodeKey.feed, content._1.toEpochSecond, content))) == 1
  }

  /** Fill the model with the database content */
  def hydrate(): Unit = {
    val values = valueOrException(redis(_.hgetall[String, String](NodeKey.node)))
    _name = values.get(StaticNodeKey.name)
    _kind = values.get(StaticNodeKey.kind)
    _createdAt = values.get(StaticNodeKey.created_at).map(OffsetDateTime.parse)
    _updatedAt = values.get(StaticNodeKey.updated_at).map(OffsetDateTime.parse)
    _refreshedAt = values.get(StaticNodeKey.refreshed_at).map(OffsetDateTime.parse)
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

  /** Return some searchable nodes. */
  def fromName(name: String): Set[Node] =
    Indexes.searchableFuzyLookup(name).map(Node(_))

  /** Node constructor. */
  def apply(nid: NID): Node = {
    new Node(nid)
  }

}