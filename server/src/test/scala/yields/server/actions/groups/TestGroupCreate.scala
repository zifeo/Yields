package yields.server.actions.groups

import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi.models._
import yields.server.dbi.tags.Tag
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}
import yields.server.dbi._
import yields.server.actions.groups._


/**
  * Test if GroupCreate action performed well
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

  val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  it should "create a group" in {
    val users: List[User] = List(User.create("e1@epfl.ch"), User.create("e2@epfl.ch"), User.create("e3@epfl.ch"), User.create("e4@epfl.ch"))
    val nodes: List[Node] = List(Group.createGroup("g1", m.sender), Group.createGroup("g2", m.sender), Group.createGroup("g3", m.sender))
    Tag.createTag("sport")
    val tags: List[String] = List("tennis", "sport", "fun")
    val action = new GroupCreate("GroupName", nodes.map(_.nid), users.map(_.uid), tags, "private")
    val res = action.run(m)
    res match {
      case GroupCreateRes(nid) =>
        val g = Group(nid)
        g.users.toSet should be(users.map(_.uid).toSet)
        g.name should be("GroupName")
        g.nodes.toSet should be(nodes.map(_.nid).toSet)
        // TODO test tags
        // val tids = tags.map((x: String) => Tag.getIdFromText(x)).filter(_.isDefined).map(_.get)
        // g.tags should be(tids.toSet)
        Tag.getIdFromText("tennis").isDefined should be(true)
        Tag.getIdFromText("fun").isDefined should be(true)
        Tag.getIdFromText("sport").isDefined should be(true)

    }
  }

  "running groupCreate without name" should "throw an exception" in {
    val users: List[User] = List(User.create("e12@email.com"), User.create("e22@email.com"), User.create("e32@email.com"), User.create("e42@email.com"))
    val nodes: List[Node] = List(Group.createGroup("g1", m.sender), Group.createGroup("g2", m.sender), Group.createGroup("g3", m.sender))
    val tags: List[String] = List("music", "acdc")
    val action = new GroupCreate("", nodes.map(_.nid), users.map(_.uid), tags, "private")
    an[ActionArgumentException] should be thrownBy action.run(m)
  }


}
