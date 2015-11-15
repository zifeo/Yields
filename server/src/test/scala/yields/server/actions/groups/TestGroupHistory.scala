package yields.server.actions.groups

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import yields.server.actions.ActionsGenerators
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal
import Arbitrary.arbitrary

object TestGroupHistory extends Properties("GroupHistory") with ActionsGenerators {

  lazy val m = new Metadata(1, Temporal.current)

  /* val propGroupHistoryWithMsg = forAll { (a: GroupHistory) =>
    val g = Group(a.nid)

    val msgList = for {
      i <- 1 until 10
      msg <- arbitrary[FeedContent].sample
    } yield msg
    msgList.foreach(g.addMessage)

    val res = a.run(m)

    res match {
      case GroupHistoryRes(x) => msgList.take(a.count)
      case _ => false
    }
  }

  val propGroupHistoryNoMsg = forAll { (a: GroupHistory) =>
    val res = a.run(m)

    res match {
      case GroupHistoryRes(List()) =>
      case _ => false
    }
  } */

}
