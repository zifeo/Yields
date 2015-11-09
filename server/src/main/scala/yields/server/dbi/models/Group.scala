package yields.server.dbi.models

import yields.server.dbi._
import yields.server.utils.Temporal

/**
 * Representation of a group
 * A group is a kind of node dedicated to chat between users
 *
 * @param nid Node id to build
 */
class Group private (override val nid: NID) extends Node

/**
 * Companion object for Group
 */
object Group {

  /**
   * Create a new group with the given name
   * @param name name of the new group
   * @return the newly created group
   */
  def createGroup(name: String): Group = {
    val group = Group(Node.newNID())
    redis.withClient { r =>
      import group.NodeKey
      val infos = List(
        (NodeKey.created_at, Temporal.current),
        (NodeKey.name, name),
        (NodeKey.kind, classOf[Group].getSimpleName)
      )
      r.hmset(group.NodeKey.node, infos)
    }
    group
  }

  def apply(nid: NID): Group = {
    new Group(nid)
  }

}