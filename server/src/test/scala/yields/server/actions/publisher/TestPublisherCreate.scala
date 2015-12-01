package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.groups.GroupCreate
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestPublisherCreate extends DBFlatSpec with Matchers with AllGenerators {

  "PublisherCreate" should "create a publisher" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = List[UID](4, 5, 6)
    val nodes = List[NID](7, 8, 9)
    user.addEntourage(users)

    val action = PublisherCreate("name", users, nodes)

    action.run(meta) match {
      case PublisherCreateRes(nid) =>
        val publisher = Publisher(nid)
        publisher.name should be("name")
        publisher.users should contain theSameElementsAs (user.uid :: users).distinct
        publisher.nodes should contain theSameElementsAs nodes.distinct
        publisher.creator should be(user.uid)
        user.groups should contain only nid
        users.foreach { uid =>
          User(uid).groups should contain (nid)
        }
    }
  }

  it should "not accept users outside entourage" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = List[UID](4, 5, 6)
    val action = new PublisherCreate("name", users, List.empty)

    val error = the[UnauthorizedActionException] thrownBy action.run(meta)
    error.getMessage should be("users must be in sender's entourage")

  }

  it should "not accept adding private node" in {

  }

}
