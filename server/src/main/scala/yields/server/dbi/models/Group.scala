package yields.server.dbi.models

import yields.server.dbi._
import yields.server.utils.Temporal
import com.redis.serialization.Parse.Implicits._

/**
  * Representation of a group
  * A group is a kind of node dedicated to chat between users
  *
  * nodes:[nid]:admins    zset
  * nodes:[nid]:tags      set
  *
  * @param nid Node id to build
  */
class Group private(override val nid: NID) extends Node {

  object GroupKey {
    val node = NodeKey.node
    val admins = s"$node:admins"
    val tags = s"$node:tags"

    /** can only be "private" or "public" */
    val visibility = "visibility"
  }

  private var _admins: Option[List[UID]] = None
  private var _visibility: Option[String] = None
  private var _tags: Option[Set[TID]] = None

  /** add admin */
  def addAdmin(id: UID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zadd(GroupKey.admins, Temporal.current.toEpochSecond, id)))

  /** remove admin */
  def removeAdmin(id: UID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zrem(GroupKey.admins, id)))

  /** admins getter */
  def admins: List[UID] = _admins.getOrElse {
    _admins = redis.withClient(_.zrange[UID](GroupKey.admins, 0, -1))
    valueOrDefault(_admins, List.empty)
  }

  /** set visibility */
  def visibility_=(visibility: String): Unit = {
    redis.withClient(_.hset(NodeKey.node, GroupKey.visibility, visibility))
    _visibility = Some(visibility)
  }

  /** get visibility */
  def visibility: String = {
    _visibility = redis.withClient(_.hget[String](NodeKey.node, GroupKey.visibility))
    valueOrDefault(_visibility, "")
  }

  /** add tags to group */
  def addTags(tags: Seq[TID]): Unit = {
    tags.foreach { x =>
      redis.withClient(_.sadd(GroupKey.tags, x))
    }
  }

  /** get the tags of a group */
  def tags: Set[TID] = _tags.getOrElse {
    val mem: Option[Set[Option[TID]]] = redis.withClient(_.smembers[TID](GroupKey.tags))
    val t: Set[TID] = mem match {
      case Some(x: Set[Option[TID]]) =>
        val defined: Set[Option[TID]] = x.filter(_.isDefined)
        val noOpt: Set[TID] = defined.map(_.get)
        noOpt
      case _ => Set()
    }
    _tags = Some(t)
    t.toSet

  }
}

/**
  * Companion object for Group
  */
object Group {

  /**
    * Create a new group with the given name
    * @param name name of the new group
    * @param creator group creator
    * @return the newly created group
    */
  def createGroup(name: String, creator: UID): Group = {
    val group = Group(Node.newNID())
    redis.withClient { r =>
      import group.NodeKey
      val infos = List(
        (NodeKey.created_at, Temporal.current),
        (NodeKey.name, name),
        (NodeKey.kind, classOf[Group].getSimpleName),
        (NodeKey.creator, creator)
      )
      r.hmset(group.NodeKey.node, infos)
    }
    group.addAdmin(creator)
    group
  }

  def apply(nid: NID): Group = {
    new Group(nid)
  }

}