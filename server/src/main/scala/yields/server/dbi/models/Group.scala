package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.exceptions.UnincrementalIdentifier

class Group private (override val nid: NID) extends Node

object Group {

  def createGroup(name: String): Group = {
    val nid = redis.incr("nodes:nid").getOrElse(throw new UnincrementalIdentifier)
    new Group(nid)
  }

  def apply(nid: NID): Group = {
    new Group(nid)
  }

}