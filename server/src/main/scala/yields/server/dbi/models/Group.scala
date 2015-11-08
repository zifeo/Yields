package yields.server.dbi.models

import yields.server.dbi._
import yields.server.utils.Temporal

class Group private (override val nid: NID) extends Node

object Group {

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