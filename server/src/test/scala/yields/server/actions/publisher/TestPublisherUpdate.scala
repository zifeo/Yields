package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.groups.GroupUpdate
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestPublisherUpdate extends DBFlatSpec with Matchers with AllGenerators {

  it should "change only the name" in {
    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)

    val newName = "newName"
    val action = new PublisherUpdate(start.nid, Some(newName), None, List.empty, List.empty, List.empty, List.empty,
      List.empty, List.empty)
    action.run(meta)

    val end = Publisher(start.nid)
    end.name should be(newName)
    end.pic should be(start.pic)
    end.users should be(start.users)
    end.nodes should be(start.nodes)
    end.tags should be(start.tags)

  }

  it should "change only the pic" in {

    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)
    start.picSetter(Array[Byte](2, 1), meta.client)

    val newPic = Array[Byte](1, 2)
    val action = new PublisherUpdate(start.nid, None, Some(newPic), List.empty, List.empty, List.empty, List.empty,
      List.empty, List.empty)
    action.run(meta)

    val end = Publisher(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    end.users should be(start.users)
    end.nodes should be(start.nodes)
    end.tags should be(start.tags)

  }

  it should "only change users" in {
    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)

    val newUsers = List[UID](2, 3, 4)
    val addAction = new PublisherUpdate(start.nid, None, None, newUsers, List.empty, List.empty, List.empty,
      List.empty, List.empty)
    addAction.run(meta)

    val middle = Publisher(start.nid)
    middle.name should be(start.name)
    middle.pic should be(start.pic)
    middle.users should be(meta.client :: newUsers)
    middle.nodes should be(start.nodes)
    middle.tags should be(start.tags)

    val oldUsers = List[UID](3)
    val removeAction = new PublisherUpdate(start.nid, None, None, List.empty, oldUsers, List.empty, List.empty,
      List.empty, List.empty)
    removeAction.run(meta)

    val end = Publisher(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    end.users should be(meta.client :: newUsers.diff(oldUsers))
    end.nodes should be(start.nodes)
    end.tags should be(start.tags)

  }

  it should "only change nodes" in {
    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)

    val newNodes = List[NID](2, 3, 4)
    val addAction = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, newNodes, List.empty,
      List.empty, List.empty)
    addAction.run(meta)

    val middle = Publisher(start.nid)
    middle.name should be(start.name)
    middle.pic should be(start.pic)
    middle.users should be(start.users)
    middle.nodes should be(newNodes)
    middle.tags should be(start.tags)

    val oldNodes = List[NID](3)
    val removeAction = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, List.empty, oldNodes,
      List.empty, List.empty)
    removeAction.run(meta)

    val end = Publisher(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    end.users should be(start.users)
    end.nodes should be(newNodes.diff(oldNodes))
    end.tags should be(start.tags)
  }

  it should "only change tags" in {
    val meta = Metadata.now(0)
    val start = Publisher.create("name1", meta.client)

    val newTags = List("tennis", "foot", "volley")
    val addAction = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, List.empty, List.empty,
      newTags, List.empty)
    addAction.run(meta)

    val middle = Publisher(start.nid)
    middle.name should be(start.name)
    middle.pic should be(start.pic)
    middle.users should be(start.users)
    middle.nodes should be(start.nodes)
    middle.tags should contain theSameElementsAs (newTags)

    val oldTags = List("foot")
    val removeAction = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, List.empty, List.empty,
      List.empty, oldTags)
    removeAction.run(meta)

    val end = Publisher(start.nid)
    end.name should be(start.name)
    end.pic should be(start.pic)
    middle.users should be(start.users)
    end.nodes should be(start.nodes)
    end.tags should contain theSameElementsAs (newTags.diff(oldTags))
  }

  it should "not be updated by someone who cannot publish" in {
    val meta = Metadata.now(0)
    val start = Group.create("name1", meta.client + 1)
    val action = new PublisherUpdate(start.nid, None, None, List.empty, List.empty, List.empty, List.empty,
      List.empty, List.empty)

    an[UnauthorizedActionException] should be thrownBy action.run(meta)

  }


}
