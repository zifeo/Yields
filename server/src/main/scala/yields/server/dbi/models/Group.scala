package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal
import com.redis.serialization.Parse.Implicits._

/**
  * Representation of a group.
  * A group is a kind of node dedicated to chat between users with eventual influx from nodes.
  * It is always private.
  *
  * @param nid Node id to build
  */
final class Group private (nid: NID) extends Node(nid)

/** [[Group]] companion object. */
object Group {

  /**
    * Create a new group with the given name.
    * @param name name of the new group
    * @param creator group creator
    * @return the newly created group
    */
  def create(name: String, creator: UID): Group = {
    val group = Group(newIdentity())
    redis { r =>
      val now = Temporal.now
      val infos = List(
        (StaticNodeKey.name, name),
        (StaticNodeKey.kind, classOf[Group].getSimpleName),
        (StaticNodeKey.creator, creator),
        (StaticNodeKey.created_at, now),
        (StaticNodeKey.refreshed_at, now),
        (StaticNodeKey.updated_at, now)
      )
      r.hmset(group.NodeKey.node, infos)
    }
    group.addUser(creator)
    group
  }

  /** [[Group]] constructor. */
  def apply(nid: NID): Group = {
    new Group(nid)
  }

}