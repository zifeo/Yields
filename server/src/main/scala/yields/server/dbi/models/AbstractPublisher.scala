package yields.server.dbi.models

import yields.server.dbi._


/**
  * Represents a publisher
  *
  * A publisher is a kind of node that is added to other private nodes
  * When a user publish in one of this special node it's spread into each
  * node that registered it as a publisher
  *
  * Some fields used from the Node class see their purpose change a bit
  * List[UID]     -> List of user that can publish in it
  * List[NID]     -> List of nodes that registered it
  */
abstract class AbstractPublisher extends Node {

  /** Add message */
  override def addMessage(content: IncomingFeedContent): Boolean = {
    val done = super.addMessage(content)
    if (done)
      broadcast(content)
    done
  }

  private def broadcast(content: IncomingFeedContent): Unit = {
    for {
      nid <- nodes
      group = Group(nid)
    } yield group.addMessage(content)
  }
}
