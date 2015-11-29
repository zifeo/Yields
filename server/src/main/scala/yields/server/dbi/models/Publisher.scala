package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal

case class Publisher private(override val nid: NID) extends AbstractPublisher(nid) {

}

object Publisher {

  def createPublisher(name: String): Publisher = {
    val publisher = Publisher(newIdentity())

    redis.withClient { r =>
      val infos = List(
        (StaticNodeKey.created_at, Temporal.now),
        (StaticNodeKey.name, name),
        (StaticNodeKey.kind, classOf[Group].getSimpleName)
      )
      r.hmset(publisher.NodeKey.node, infos)
    }
    publisher
  }

  def apply(nid: NID): Publisher = {
    new Publisher(nid)
  }

}