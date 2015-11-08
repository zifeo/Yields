package yields.server.dbi.models

class Group private (override val nid: NID) extends Node

object Group {

  def createGroup(name: String): Group = {
    Group(Node.newNID())
  }

  def apply(nid: NID): Group = {
    new Group(nid)
  }

}