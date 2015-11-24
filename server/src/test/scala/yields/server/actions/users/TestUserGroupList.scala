package yields.server.actions.users

import org.scalatest.Matchers
import yields.server._
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Test class User group list
  * TODO implement user group list correctly
  */
class TestUserGroupList extends DBFlatSpec with Matchers with AllGenerators {

  lazy val m = sample[Metadata]

  it should "return empty list when the group list is empty" in {
    val u = User.create("an@SAs678email.com")
    val action = new UserGroupList(u.uid)
    val res = action.run(m)
    res match {
      case UserGroupListRes(x) =>
        x.length should be(0)
    }
  }

  it should "return the group list when it is not empty" in {
    val u = User.create("an@email.com")
    val g1 = Group.createGroup("g1", m.client)
    g1.name = "n1"
    val g2 = Group.createGroup("g2", m.client)
    g2.name = "n2"
    u.addGroup(g1.nid)
    u.addGroup(g2.nid)
    val action = new UserGroupList(u.uid)
    val res = action.run(m)
    res match {
      case UserGroupListRes(x) =>
        x.length should be(2)
        x.map(_._1).toSet should contain(g1.nid)
        x.map(_._1).toSet should contain(g2.nid)
        x.map(_._2).toSet should contain(g1.name)
        x.map(_._2).toSet should contain(g2.name)
    }
  }

}
