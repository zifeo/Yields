package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.groups.GroupCreate
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestPublisherCreate extends DBFlatSpec with Matchers with AllGenerators {

  "publisherCreate" should "create a publisher" in {
    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = sample[List[UID]]
    val nodes = sample[List[NID]]
    user.addEntourage(users)

    val action = PublisherCreate("name", users, nodes)

    action.run(meta) match {
      case PublisherCreateRes(nid) =>
        val publisher = Publisher(nid)
        publisher.name should be("name")
        publisher.users should contain theSameElementsAs users.distinct
        publisher.nodes should contain theSameElementsAs nodes.distinct
        publisher.creator should be(user.uid)
    }
  }

  it should "not accept users outside entourage" in {
    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = sample[List[UID]]
    val action = new PublisherCreate("name", users, List.empty)

    val error = the[UnauthorizedActionException] thrownBy action.run(meta)
    error.getMessage should be("users must be in sender's entourage")
  }

}
