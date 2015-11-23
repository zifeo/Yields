package yields.server.actions.groups

import org.scalatest.Matchers
import yields.server._
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi._
import yields.server.dbi.models.{Group, Node, User}
import yields.server.dbi.tags.Tag
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Test if GroupCreate action performed well
  * TODO test GroupCreate without users
  */
class TestGroupCreate extends DBFlatSpec with Matchers with AllGenerators {

  lazy val m = sample[Metadata]

  it should "create a group" in {
    val u = User.create("email@email.com")
    val meta = new Metadata(u.uid, Temporal.now, Temporal.now)
    val users: List[User] = List(User.create("e1@epfl.ch"), User.create("e2@epfl.ch"), User.create("e3@epfl.ch"), User.create("e4@epfl.ch"))
    val nodes: List[Node] = List(Group.createGroup("g1", m.client), Group.createGroup("g2", m.client), Group.createGroup("g3", m.client))
    Tag.createTag("sport")
    val tags: List[String] = List("tennis", "sport", "fun")
    val action = new GroupCreate("GroupName", nodes.map(_.nid), users.map(_.uid), tags, "private")
    val res = action.run(meta)
    res match {
      case GroupCreateRes(nid) =>
        val g = Group(nid)
        g.users.toSet should be(users.map(_.uid).toSet)
        g.name should be("GroupName")
        g.nodes.toSet should be(nodes.map(_.nid).toSet)
        val tids = tags.map((x: String) => Tag.getIdFromText(x)).filter(_.isDefined).map(_.get)
        g.tags should be(tids.toSet)
        Tag.getIdFromText("tennis").isDefined should be(true)
        Tag.getIdFromText("fun").isDefined should be(true)
        Tag.getIdFromText("sport").isDefined should be(true)
        val creator = User(u.uid)
        creator.groups should contain(nid)
    }
  }

  "running groupCreate without name" should "throw an exception" in {
    val users: List[User] = List(
      User.create("e12@email.com"),
      User.create("e22@email.com"),
      User.create("e32@email.com"),
      User.create("e42@email.com"))
    val nodes: List[Node] = List(Group.createGroup("g1", m.client), Group.createGroup("g2", m.client), Group.createGroup("g3", m.client))
    val tags: List[String] = List("music", "acdc")
    val action = new GroupCreate("", nodes.map(_.nid), users.map(_.uid), tags, "private")
    an[ActionArgumentException] should be thrownBy action.run(m)
  }


}
