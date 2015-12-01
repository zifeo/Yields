package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal

/**
  * Representation of a publisher.
  * A group is a kind of node dedicated to chat between users with eventual influx from nodes.
  * It is always public.
  *
  * @param nid Node id to build
  */
final class Publisher private(nid: NID) extends AbstractPublisher(nid) {}

/** [[Publisher]] companion object. */
object Publisher {

  /**
    * Create a new publisher with the given name.
    * @param name name of the new publisher
    * @param creator publisher creator
    * @return the newly created publisher
    */
  def create(name: String, creator: UID): Publisher = {
    val publisher = Publisher(newIdentity())
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
      r.hmset(publisher.NodeKey.node, infos)
    }
    publisher.addUser(creator)
    publisher
  }

  /** [[Publisher]] constructor. */
  def apply(nid: NID): Publisher = {
    new Publisher(nid)
  }

}