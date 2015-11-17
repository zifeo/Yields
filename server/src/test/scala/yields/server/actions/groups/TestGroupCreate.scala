package yields.server.actions.groups

import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi.models.{Node, Group, UID, User}
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}
import yields.server.dbi._


/**
  * Test if GroupCreate action performed well
  * TODO flush database before and after
  * TODO test GroupCreate without users
  */
class TestGroupCreate extends FlatSpec with Matchers with BeforeAndAfter {

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

  "running groupCreate action with all parameters set" should "create a group" in {
    val users: List[User] = List(User.create("e1@epfl.ch"), User.create("e2@epfl.ch"), User.create("e3@epfl.ch"), User.create("e4@epfl.ch"))
    val nodes: List[Node] = List(Group.createGroup("g1"), Group.createGroup("g2"), Group.createGroup("g3"))
    val action = new GroupCreate("GroupName", nodes.map(_.nid), users.map(_.uid))
    val res = action.run(m)
    res match {
      case GroupCreateRes(nid) =>
        val g = Group(nid)
        g.users.toSet should be(users.map(_.uid).toSet)
        g.name should be("GroupName")
        g.nodes.toSet should be(nodes.map(_.nid).toSet)
    }
  }

  "running groupCreate without name" should "throw an exception" in {
    val users: List[User] = List(User.create("e12@email.com"), User.create("e22@email.com"), User.create("e32@email.com"), User.create("e42@email.com"))
    val nodes: List[Node] = List(Group.createGroup("g1"), Group.createGroup("g2"), Group.createGroup("g3"))
    val action = new GroupCreate("", nodes.map(_.nid), users.map(_.uid))
    an[ActionArgumentException] should be thrownBy action.run(m)
  }


}
