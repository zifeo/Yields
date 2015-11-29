package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.actions.groups.{GroupInfoRes, GroupInfo}
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestPublisherInfo extends DBFlatSpec with Matchers with AllGenerators {

  "PublisherInfo" should "get publisher info" in {

    val meta = Metadata.now(0)
    val publisher = Publisher.createPublisher("name", meta.client)
    publisher.addUser(List[UID](3, 4, 5))
    publisher.addNode(List[NID](13, 14, 15))
    // group.pic = Array[Byte](1, 2)

    val action = PublisherInfo(publisher.nid)

    action.run(meta) match {
      case PublisherInfoRes(nid, name, pic, currentUsers, currentNodes) =>
        nid should be(publisher.nid)
        name should be(publisher.name)
        //pic should be (group.pic)
        currentUsers should contain theSameElementsAs publisher.users
        currentNodes should contain theSameElementsAs publisher.nodes
    }
  }

}
