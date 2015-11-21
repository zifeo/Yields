package yields.server.dbi.models

import yields.server.dbi._
import yields.server.utils.Temporal
import com.redis.serialization.Parse.Implicits._

/**
  * Representation of a group
  * A group is a kind of node dedicated to chat between users
  *
  * @param nid Node id to build
  */
class Group private(override val nid: NID) extends Node {

  object GroupKey {
    val admins = s"${NodeKey.node}:admins"
  }

  private var _admins: Option[List[UID]] = None

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
}

/**
  * Companion object for Group
  */
object Group {

  /**
    * Create a new group with the given name
    * @param name name of the new group
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
    group
  }

  def apply(nid: NID): Group = {
    new Group(nid)
  }

}