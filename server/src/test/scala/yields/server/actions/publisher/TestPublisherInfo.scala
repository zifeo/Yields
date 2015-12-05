package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.actions.groups.{GroupInfoRes, GroupInfo}
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.tests.AllGenerators

class TestPublisherInfo extends DBFlatSpec with Matchers with AllGenerators {

  "PublisherInfo" should "get publisher info" in {

    val meta = Metadata.now(0)
    val publisher = Publisher.create("name", meta.client)
    publisher.addUser(List[UID](3, 4, 5))
    publisher.addNode(List[NID](13, 14, 15))
    publisher.picSetter("12", meta.client)
    publisher.addTags(List("tennis", "foot"))

    val action = PublisherInfo(publisher.nid)

    action.run(meta) match {
      case PublisherInfoRes(nid, name, pic, currentUsers, currentNodes, tags) =>
        nid should be(publisher.nid)
        name should be(publisher.name)
        pic should be(publisher.pic)
        currentUsers should contain theSameElementsAs publisher.users
        currentNodes should contain theSameElementsAs publisher.nodes
        tags should contain theSameElementsAs publisher.tags

    }
  }

  it should "get limited publisher info if he does not belong to it" in {

    val meta = Metadata.now(2)
    val uid = 0
    val publisher = Publisher.create("name", uid)
    publisher.addUser(List[UID](3, 4, 5))
    publisher.addNode(List[NID](13, 14, 15))
    publisher.picSetter("12", uid)

    val action = PublisherInfo(publisher.nid)

    action.run(meta) match {
      case PublisherInfoRes(nid, name, pic, currentUsers, currentNodes) =>
        nid should be(publisher.nid)
        name should be(publisher.name)
        pic should be(publisher.pic)
        currentUsers should be (empty)
        currentNodes should contain theSameElementsAs publisher.nodes
    }
  }

}
