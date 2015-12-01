package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.groups.GroupUpdate
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestPublisherUpdate extends DBFlatSpec with Matchers with AllGenerators {

  "PublisherUpdate" should "change only the name" in {

    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)

    val newName = "newName"
    val action = new PublisherUpdate(start.nid, Some(newName), None, List.empty, List.empty, List.empty, List.empty)
    action.run(meta)

    val end = Publisher(start.nid)
    end.name should be(newName)
    end.pic should be(start.pic)
    end.users should be(start.users)
    end.nodes should be(start.nodes)

  }

  it should "change only the pic" in {

    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)
    start.picSetter("21", meta.client)

    val newPic = "12"
    val action = new PublisherUpdate(start.nid, None, Some(newPic), List.empty, List.empty, List.empty, List.empty)
    action.run(meta)

    val end = Publisher(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    end.users should be(start.users)
    end.nodes should be(start.nodes)

  }

  it should "only change users" in {

    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)

    val newUsers = List[UID](2, 3, 4)
    val addAction = new PublisherUpdate(start.nid, None, None, newUsers, List.empty, List.empty, List.empty)
    addAction.run(meta)

    val middle = Publisher(start.nid)
    middle.name should be(start.name)
    middle.pic should be(start.pic)
    middle.users should be(meta.client :: newUsers)
    middle.nodes should be(start.nodes)

    val oldUsers = List[UID](3)
    val removeAction = new PublisherUpdate(start.nid, None, None, List.empty, oldUsers, List.empty, List.empty)
    removeAction.run(meta)

    val end = Publisher(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    end.users should be(meta.client :: newUsers.diff(oldUsers))
    end.nodes should be(start.nodes)

  }

  it should "only change nodes" in {

    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)

    val newNodes = List[NID](2, 3, 4)
    val addAction = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, newNodes, List.empty)
    addAction.run(meta)

    val middle = Publisher(start.nid)
    middle.name should be(start.name)
    middle.pic should be(start.pic)
    middle.users should be(start.users)
    middle.nodes should be(newNodes)

    val oldNodes = List[NID](3)
    val removeAction = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, List.empty, oldNodes)
    removeAction.run(meta)

    val end = Publisher(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    middle.users should be(start.users)
    end.nodes should be(newNodes.diff(oldNodes))
  }

  it should "not be updated by someone who cannot publish" in {

    val meta = Metadata.now(0)
    val start = Group.create("name1", meta.client + 1)
    val action = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, List.empty, List.empty)

    val thrown = the [UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include (meta.client.toString)

  }


}
