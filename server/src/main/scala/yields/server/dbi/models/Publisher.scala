package yields.server.dbi.models

case class Publisher private(override val nid: NID) extends AbstractPublisher {

}

object Publisher {

  def createPublisher(name: String): Publisher = {
    Publisher(Node.newNID())
  }

  def apply(nid: NID) = {
    new Publisher(nid)
  }

}