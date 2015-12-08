package yields.server.actions.groups

import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec

class TestGroupCreate extends YieldsSpec {

  "GroupCreate" should "create a group" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = List[UID](4, 5, 6)
    val nodes = List[NID](7, 8, 9)
    user.addEntourage(users)

    val action = GroupCreate("GroupName", users, nodes)

    action.run(meta) match {
      case GroupCreateRes(nid) =>
        val group = Group(nid)
        group.name should be("GroupName")
        group.users should contain theSameElementsAs (user.uid :: users).distinct
        group.nodes should contain theSameElementsAs nodes.distinct
        group.creator should be(user.uid)
        user.nodes should contain only nid
        users.foreach { uid =>
          User(uid).nodes should contain (nid)
        }
    }
  }

  it should "not accept adding users out of entourage" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = List[UID](4, 5, 6)
    val action = new GroupCreate("GroupName", users, List.empty)

    val thrown = the[UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include(user.uid.toString)

  }

  it should "not accept adding private node" in {

  }

}

