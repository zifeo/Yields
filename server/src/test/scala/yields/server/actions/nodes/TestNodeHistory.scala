package yields.server.actions.nodes

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.actions.Result
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
    val group = Group.createGroup("name", m.sender)
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

  "getting messages containing media" should "give the media back" in {
    val group = Group.createGroup("name", 1)

    val messagesToReceive = List((Temporal.current, 1, None, "this entry has some text"), (Temporal.current, 2, Some("Some content"), ""),
      (Temporal.current, 3, Some("other content"), "text"), (Temporal.current, 4, None, "some text again"))

    messagesToReceive.foreach(send)

    def send(m: (OffsetDateTime, Int, Option[String], String)): Unit = {
      val t = if (m._4 == "") None else Some(m._4)
      val contentType = if (m._3.isDefined) Some("image") else None
      val addMsg = new NodeMessage(group.nid, t, contentType, m._3)
      addMsg.run(new Metadata(m._2, m._1))
    }

    val history = new NodeHistory(group.nid, Temporal.current, 4)
    val res = history.run(new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current))

    res match {
      case NodeHistoryRes(nid, messages) =>
        messages.map(x => (x._2, x._3, x._4)).reverse should be(messagesToReceive.map(x => (x._2, x._3, x._4)))
    }
  }

}
