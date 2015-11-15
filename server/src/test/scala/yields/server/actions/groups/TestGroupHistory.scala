package yields.server.actions.groups

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalatest.{Matchers, FlatSpec}
import yields.server.dbi.models.ModelsGenerators
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal
import Arbitrary.arbitrary

/**
  * Test class for group history action
  * TODO test getting messages with first tid not existing
  * TODO test getting negative number of messages
  */
class TestGroupHistory extends FlatSpec with Matchers with ModelsGenerators {

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
    val action = new GroupHistory(group.nid, 0, n)
    val res = action.run(m)
    res match {
      case GroupHistoryRes(x) =>
        x.length should be(n)
    }
  }

}
