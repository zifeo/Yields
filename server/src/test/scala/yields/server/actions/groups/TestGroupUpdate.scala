package yields.server.actions.groups

import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec
import yields.server.utils.Temporal

class TestGroupUpdate extends YieldsSpec {

  "GroupUpdate" should "change only name" in {

    val meta = Metadata.now(0)
    val start = Group.create("name1", meta.client)

    val newName = "newName"
    val action = GroupUpdate(start.nid, Some(newName), None, List.empty, List.empty, List.empty, List.empty)
    action.run(meta)

    val end = Group(start.nid)
    end.name should be(newName)
    end.pic should be(start.pic)
    end.users should be(start.users)
    end.nodes should be(start.nodes)

  }

  it should "change only pic" in {

    val meta = Metadata.now(0)
    val start = Group.create("name1", meta.client)
    start.pic("21", meta.client)

    val newPic = "12"
    val action = GroupUpdate(start.nid, None, Some(newPic), List.empty, List.empty, List.empty, List.empty)
    action.run(meta)

    val end = Group(start.nid)
    end.name should be(start.name)
    end.pic should be(newPic)
    end.users should be(start.users)
    end.nodes should be(start.nodes)

  }

  it should "change only users" in {

    val meta = Metadata.now(0)
    val start = Group.create("name1", meta.client)

    val newUsers = List[UID](2, 3, 4)
    val addAction = GroupUpdate(start.nid, None, None, newUsers, List.empty, List.empty, List.empty)
    addAction.run(meta)

    val middle = Group(start.nid)
    middle.name should be(start.name)
    middle.pic should be(start.pic)
    middle.users should be(meta.client :: newUsers)
    middle.nodes should be(start.nodes)
    newUsers.foreach { uid =>
      User(uid).nodes should contain(middle.nid)
    }

    val oldUsers = List[UID](3)
    val removeAction = GroupUpdate(start.nid, None, None, List.empty, oldUsers, List.empty, List.empty)
    removeAction.run(meta)

    val end = Group(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    end.users should be(meta.client :: newUsers.diff(oldUsers))
    end.nodes should be(start.nodes)
    oldUsers.foreach { uid =>
      User(uid).nodes should not contain end.nid
    }

  }

  it should "change only nodes" in {

    val meta = Metadata.now(0)
    val start = Group.create("name1", meta.client)

    val newNodes = List[NID](2, 3, 4)
    val addAction = GroupUpdate(start.nid, None, None, List.empty, List.empty, newNodes, List.empty)
    addAction.run(meta)

    val middle = Group(start.nid)
    middle.name should be(start.name)
    middle.pic should be(start.pic)
    middle.users should be(start.users)
    middle.nodes should be(newNodes)

    val oldNodes = List[NID](3)
    val removeAction = GroupUpdate(start.nid, None, None, List.empty, List.empty, List.empty, oldNodes)
    removeAction.run(meta)

    val end = Group(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    middle.users should be(start.users)
    end.nodes should be(newNodes.diff(oldNodes))

  }

  "updating the group" should "also update updated_at field" in {
    val meta = Metadata.now(0)
    val group = Group.create("name", meta.client)
    val up1 = group.updated_at
    val action = GroupUpdate(group.nid, Some("newName"), None, List.empty, List.empty, List.empty, List.empty)
    action.run(meta)
    group.updated_at should not be up1
  }

  it should "not be changed by someone out of the users" in {

    val meta = Metadata.now(0)
    val start = Group.create("name1", meta.client + 1)
    val action = GroupUpdate(start.nid, None, None, List.empty, List.empty, List.empty, List.empty)

    val thrown = the[UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include(meta.client.toString)

  }

}
