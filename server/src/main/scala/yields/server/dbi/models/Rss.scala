package yields.server.dbi.models

case class Rss private(override val nid: NID) extends AbstractPublisher {

}

object Rss {
  def createRss(name: String, url: String): Rss = {
    Rss(Node.newNID())
  }

  def apply(nid: NID) = {
    new Rss(nid)
  }
}