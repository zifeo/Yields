package yields.server.actions.nodes

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.dbi._
import yields.server.dbi.models.{ModelsGenerators, _}
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

/**
  * Test class for group history action
  * TODO test getting messages with first tid not existing
  * TODO test getting negative number of messages
  */
class TestNodeHistory extends FlatSpec with Matchers with ModelsGenerators with BeforeAndAfter {

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

  val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  def add10Msgs(nid: NID) = {
    val g = Group(nid)
    for {
      i <- 1 until 10
      msg <- arbitrary[IncomingFeedContent].sample
    } yield g.addMessage(msg)
  }

  "retrieving n messages from a group" should "return the n messages" in {
    val group = Group.createGroup("name")
    add10Msgs(group.nid)
    val n = 5
    val action = new NodeHistory(group.nid, Temporal.current, n)
    val res = action.run(m)
    res match {
      case NodeHistoryRes(nid, messages) =>
        messages.length should be(n)
        nid should be(group.nid)
    }
  }

  "getting message containing media" should "give the media back" in {
    val group = Group.createGroup("name")
    val addMsg1 = new NodeMessage(group.nid, Some("this entry has some text"), None, None)
    addMsg1.run(new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current))

    val addMsg2 = new NodeMessage(group.nid, None, Some("image"), Some("Some content"))
    addMsg2.run(new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current))

    val addMsg3 = new NodeMessage(group.nid, Some("text"), Some("image"), Some("other content"))
    addMsg3.run(new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current))

    val addMsg4 = new NodeMessage(group.nid, Some("some text again"), None, None)
    addMsg4.run(new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current))

    val history = new NodeHistory(group.nid, Temporal.current, 4)
    val res = history.run(new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current))
    res match {
      case NodeHistoryRes(nid, messages) =>
        messages.head._4 should be("this entry has some text")
        messages.head._3.isDefined should be(false)

        messages(1)._3.isDefined should be(true)
        messages(1)._3.get should be("Some content")
        messages(1)._4 should be("")

        messages(2)._3.isDefined should be(true)
        messages(2)._3.get should be("other content")
        messages(2)._4 should be("text")

        messages(3)._3.isDefined should be(false)
        messages(3)._4 should be("some text again")
    }
  }

}
