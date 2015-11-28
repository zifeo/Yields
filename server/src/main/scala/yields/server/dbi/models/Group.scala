package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal
import com.redis.serialization.Parse.Implicits._

/**
  * Representation of a group
  * A group is a kind of node dedicated to chat between users
  *
  * @param nid Node id to build
  */
final class Group private (override val nid: NID) extends Node

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
    val group = Group(newIdentity())
    redis.withClient { r =>
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
    group
  }

  def apply(nid: NID): Group = {
    new Group(nid)
  }

}