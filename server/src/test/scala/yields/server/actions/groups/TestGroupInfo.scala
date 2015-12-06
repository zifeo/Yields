package yields.server.actions.groups

import org.scalatest.Matchers
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestGroupInfo extends DBFlatSpec with Matchers {

  "GroupInfo" should "get group info" in {

    val meta = Metadata.now(0)
    val group = Group.create("GroupName", meta.client)
    group.addUser(List[UID](3, 4, 5))
    group.addNode(List[NID](13, 14, 15))
    group.pic("12", meta.client)

    val action = GroupInfo(group.nid)

    action.run(meta) match {
      case GroupInfoRes(nid, name, pic, currentUsers, currentNodes) =>
        nid should be (group.nid)
        name should be (group.name)
        pic should be (group.pic)
        currentUsers should contain theSameElementsAs group.users
        currentNodes should contain theSameElementsAs group.nodes
    }
  }

  it should "not access info if he does not belong to the groups" in {

    val meta = Metadata.now(0)
    val group = Group.create("GroupName", meta.client + 1)
    val action = GroupInfo(group.nid)

    val thrown = the [UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include (meta.client.toString)

  }

}
