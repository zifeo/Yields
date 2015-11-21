package yields.server.actions.nodes

import org.scalacheck.Arbitrary._
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.actions.groups.{GroupCreate, GroupCreateRes}
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

class TestNodeMessage extends FlatSpec with Matchers with BeforeAndAfter {

  /** Switch on test database */
  before {
    redis(_.select(Config.getInt("test.database.id")))
    redis(_.flushdb)
  }

  /** Switch back on main database */
  after {
    redis(_.flushdb)
    redis(_.select(Config.getInt("database.id")))
  }

  lazy val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)
  lazy val users: List[User] = List(User.create("e1"), User.create("e2"), User.create("e3"), User.create("e4"))
  lazy val nodes: List[Node] = List(Group.createGroup("g1", m.sender), Group.createGroup("g2", m.sender), Group.createGroup("g3", m.sender))

  "running nodeMessage action on a group" should "add new entry to feed" in {
    val create = new GroupCreate("GroupName", nodes.map(_.nid), users.map(_.uid))
    val res = create.run(m)

    res match {
      case GroupCreateRes(nid) =>
        val action = new NodeMessage(nid, Some("A message"), None, None)
        action.run(m)
        val g = Group(nid)
        val msg = g.getMessagesInRange(Temporal.current, 100)
        msg.length should be(1)
        msg.head._4 should be("A message")
        msg.head._2 should be(m.sender)
    }
  }

  "receiving a media in a messgae" should "create the media on disk" in {
    val group = Group.createGroup("name", m.sender)
    val action = new NodeMessage(group.nid, Some("Some text"), Some("image"), Some("Some content"))
    action.run(m)
    val feed = group.getMessagesInRange(Temporal.current, 5)

    feed.head._3.isDefined should be(true)
    feed.head._4 should be("Some text")

    val media = Media(feed.head._3.get)
    Media.checkFileExist(media.hash) should be(true)
    media.contentType should be("image")
    media.content should be("Some content")
  }


}
