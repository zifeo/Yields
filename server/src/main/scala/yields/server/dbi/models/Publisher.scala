package yields.server.dbi.models

import yields.server.dbi._
import yields.server.utils.Temporal

case class Publisher private(override val nid: NID) extends AbstractPublisher {

}

object Publisher {

  def createPublisher(name: String): Publisher = {
    val publisher = Publisher(Node.newNID())
    
    redis.withClient { r =>
      import publisher.NodeKey
      val infos = List(
        (NodeKey.created_at, Temporal.now),
        (NodeKey.name, name),
        (NodeKey.kind, classOf[Group].getSimpleName)
      )
      r.hmset(publisher.NodeKey.node, infos)
    }
    publisher
  }

  def apply(nid: NID): Publisher = {
    new Publisher(nid)
  }

}