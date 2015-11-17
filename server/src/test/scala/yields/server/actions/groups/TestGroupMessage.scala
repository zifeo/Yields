package yields.server.actions.groups

import org.scalacheck.Arbitrary._
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

class TestGroupMessage extends FlatSpec with Matchers with BeforeAndAfter {

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
  lazy val users: List[User] = List(User.create("e1"), User.create("e2"), User.create("e3"), User.create("e4"))
  lazy val nodes: List[Node] = List(Group.createGroup("g1"), Group.createGroup("g2"), Group.createGroup("g3"))

  "running groupCreate action with all parameters set" should "create a group" in {
    val create = new GroupCreate("GroupName", nodes.map(_.nid), users.map(_.uid))
    val res = create.run(m)

    res match {
      case GroupCreateRes(nid) =>
        val action = new GroupMessage(nid, "A message")
        action.run(m)
        val g = Group(nid)
        val msg = g.getMessagesInRange(0, 100)
        msg.length should be(1)
        msg.head._4 should be("A message")
        msg.head._2 should be(m.sender)
    }

  }

}
