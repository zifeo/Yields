package yields.server.actions.groups

import org.scalatest.Matchers
import yields.server._
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Test class for Group Update action
  * TODO updating picture
  */
class TestGroupUpdate extends DBFlatSpec with Matchers with AllGenerators {

  lazy val m = sample[Metadata]

  it should "update the group in db" in {
    val g1 = Group.createGroup("name1", m.client)
    val action = new GroupUpdate(g1.nid, Some("name2"), None)
    action.run(m)
    val g2 = Group(g1.nid)
    g2.name should be("name2")
  }

}
