package yields.server.actions.users

import org.scalacheck.Arbitrary._
import org.scalacheck.Properties
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.actions.ActionsGenerators
import yields.server.dbi._
import yields.server.dbi.exceptions.KeyNotSetException
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

/**
  * Test class User group list
  * TODO implement user group list correctly
  */
class TestUserGroupList extends FlatSpec with Matchers with BeforeAndAfter {

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

  "Test with empty groupList" should "return empty list" in {
    val u = User.create("an@SAs678email.com")
    val action = new UserGroupList(u.uid)
    val res = action.run(m)
    res match {
      case UserGroupListRes(x) =>
        x.length should be(0)
    }
  }

  /* "Test with 2 groups in list" should "return the group list" in {
    val u = User.create("an@email.com")
    val g1 = Group.createGroup("g1")
    val g2 = Group.createGroup("g2")
    u.addToGroups(g1.nid)
    u.addToGroups(g2.nid)
    val action = new UserGroupList(u.uid)
    val res = action.run(m)
    res match {
      case UserGroupListRes(x) =>
        x.length should be(2)
         x.toSet should contain((g1.nid, g1.name))
        x.toSet should contain((g2.nid, g2.name))
    }
  } */

}
