package yields.server.actions.groups

import org.scalacheck.Arbitrary._
import org.scalatest.Matchers
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Test class for Group Update action
  * TODO updating picture
  */
class TestGroupUpdate extends DBFlatSpec with Matchers {

  lazy val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  it should "update the group in db" in {
    val g1 = Group.createGroup("name1", m.sender)
    val action = new GroupUpdate(g1.nid, Some("name2"), None)
    action.run(m)
    val g2 = Group(g1.nid)
    g2.name should be("name2")
  }

}
