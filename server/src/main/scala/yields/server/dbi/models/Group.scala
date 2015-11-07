package yields.server.dbi.models

import yields.server.dbi._

class Group private (override val nid: NID) extends Node {

}

object Group {

  def createGroup(name: String): NID = {
    val lastNid:Long = redis.incr("nodes:nid").getOrElse(-1)
    if(lastNid > 0) {
      new Group(lastNid)
      lastNid
    } else {
      throw new RedisNotAvailableException
    }
  }

  def apply(n: NID): Group = {
    new Group(n)
  }

}