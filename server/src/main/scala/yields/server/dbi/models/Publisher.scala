package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.utils.Temporal

/**
  * Representation of a publisher.
  * A group is a kind of node dedicated to chat between users with eventual influx from nodes.
  * It is always public.
  * When a user publish in one of this special node it's spread into each
  * node that registered it as a publisher
  *
  * Some fields used from the Node class see their purpose change a bit
  * List[UID]     -> List of user that can publish in it
  * List[NID]     -> List of nodes that registered it
  *
  * @param nid Node id to build
  */
final class Publisher private(nid: NID) extends Node(nid) with Tags {

  val nodeKey = NodeKey.node
  override val nodeID = nid

  /** Name setter. */
  override def name_=(n: String): Unit = {
    Indexes.searchableUnregister(name, nid)
    super.name_=(n)
    Indexes.searchableRegister(name, nid)
  }

  /** Add message */
  override def addMessage(content: FeedContent): Boolean = {
    val parent = super.addMessage(content)
    val children = for (node <- receivers if parent) yield {
      Node(node).addMessage(content.copy(_2 = nid))
    }
    children.foldLeft(parent)(_ && _)
  }

}

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
    val now = Temporal.now
    val infos = List(
      (StaticNodeKey.name, name),
      (StaticNodeKey.kind, classOf[Group].getSimpleName),
      (StaticNodeKey.creator, creator),
      (StaticNodeKey.created_at, now),
      (StaticNodeKey.refreshed_at, now),
      (StaticNodeKey.updated_at, now)
    )
    assert(redis(_.hmset(publisher.NodeKey.node, infos)))
    assert(Indexes.searchableRegister(name, publisher.nid))
    publisher.addUser(creator)
    publisher
  }

  /** [[Publisher]] constructor. */
  def apply(nid: NID): Publisher = {
    new Publisher(nid)
  }

}