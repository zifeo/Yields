package yields.server.actions.groups

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.actions.nodes.{NodeHistoryRes, NodeHistory}
import yields.server.dbi._
import yields.server.dbi.models.ModelsGenerators
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}
import Arbitrary.arbitrary

/**
  * Test class for group history action
  * TODO test getting messages with first tid not existing
  * TODO test getting negative number of messages
  */
class TestGroupHistory extends FlatSpec with Matchers with ModelsGenerators with BeforeAndAfter {

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

  def add10Msgs(nid: NID) = {
    val g = Group(nid)
    for {
      i <- 1 until 10
      msg <- arbitrary[FeedContent].sample
    } yield g.addMessage(msg)
  }

  "retrieving n messages from a group" should "return the n messages" in {
    val group = Group.createGroup("name")
    add10Msgs(group.nid)
    val n = 5
<<<<<<< HEAD
    val action = new GroupHistory(group.nid, Temporal.current, n)
    val res = action.run(m)
    res match {
      case GroupHistoryRes(nid, messages) =>
        messages.length should be(n)
        nid should be (group.nid)
=======
    val action = new NodeHistory(group.nid, 1, n)
    val res = action.run(m)
    res match {
      case NodeHistoryRes(x) =>
        x.length should be(n)
>>>>>>> massive refactor, image -> media
    }
  }

}
