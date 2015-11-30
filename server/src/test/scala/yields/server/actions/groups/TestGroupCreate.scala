package yields.server.actions.groups

import org.scalatest.Matchers
import yields.server._
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.dbi.tags.Tag
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

class TestGroupCreate extends DBFlatSpec with Matchers with AllGenerators {

  "GroupCreate" should "create a group" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = sample[List[UID]]
    val nodes = sample[List[NID]]
    user.addEntourage(users)

    val action = GroupCreate("GroupName", users, nodes)

    action.run(meta) match {
      case GroupCreateRes(nid) =>
        val group = Group(nid)
        group.name should be("GroupName")
        group.users should contain theSameElementsAs (user.uid :: users).distinct
        group.nodes should contain theSameElementsAs nodes.distinct
        group.creator should be(user.uid)
        user.groups should contain only nid
    }
  }

  it should "not accept adding users out of entourage" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = sample[List[UID]]
    val action = new GroupCreate("GroupName", users, List.empty)

    val thrown = the[UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include(user.uid.toString)

  }

  it should "not accept adding private node" in {

  }

}

