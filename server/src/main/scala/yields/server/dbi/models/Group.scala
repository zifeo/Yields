package yields.server.dbi.models

class Group private(override val nid: NID) extends Node

object Group {

  def createGroup(name: String): Group = {
    val group = Group(Node.newNID())
    group.name_(name)
    group.kind_("Group")
    group
  }

  def apply(nid: NID): Group = {
    new Group(nid)
  }

}