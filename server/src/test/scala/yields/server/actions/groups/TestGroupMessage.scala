package yields.server.actions.groups

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.actions.nodes.NodeMessage
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

class TestGroupMessage extends DBFlatSpec with Matchers with AllGenerators {

  val m = sample[Metadata]

  /*it should "add new entry to feed" in {
    val users = List(
      User.create("e1"),
      User.create("e2"),
      User.create("e3"),
      User.create("e4")
    )
    val nodes = List(
      Group.createGroup("g1", m.client),
      Group.createGroup("g2", m.client),
      Group.createGroup("g3", m.client)
    )
    val tags = List("nature")
    val create = new GroupCreate("GroupName", nodes.map(_.nid), users.map(_.uid), tags, "private")
    val res = create.run(m)

    res match {
      case GroupCreateRes(nid) =>
        val action = new NodeMessage(nid, Some("A message"), None, None)
        action.run(m)
        val g = Group(nid)
        val msg = g.getMessagesInRange(Temporal.now, 100)
        msg.length should be(1)
        msg.head._4 should be("A message")
        msg.head._2 should be(m.client)
    }
  }

  it should "create the media on disk" in {
    val group = Group.createGroup("name", m.client)
    val action = new NodeMessage(group.nid, Some("Some text"), Some("image"), Some("Some content"))
    action.run(m)
    val feed = group.getMessagesInRange(Temporal.now, 5)

    feed.head._3.isDefined should be(true)
    feed.head._4 should be("Some text")

    val media = Media(feed.head._3.get)
    Media.checkFileExist(media.hash) should be(true)
    media.contentType should be("image")
    media.content should be("Some content")
  }*/


}
