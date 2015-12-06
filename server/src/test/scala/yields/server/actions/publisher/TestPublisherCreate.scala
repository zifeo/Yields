package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.groups.GroupCreate
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.tests.AllGenerators

class TestPublisherCreate extends DBFlatSpec with Matchers with AllGenerators {

  "PublisherCreate" should "create a publisher" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = List[UID](4, 5, 6)
    val nodes = List[NID](7, 8, 9)
    val tags = List("a", "b", "c")
    user.addEntourage(users)

    val action = PublisherCreate("name", users, nodes, tags)

    action.run(meta) match {
      case PublisherCreateRes(nid) =>
        val publisher = Publisher(nid)
        publisher.name should be("name")
        publisher.users should contain theSameElementsAs (user.uid :: users).distinct
        publisher.nodes should contain theSameElementsAs nodes.distinct
        publisher.creator should be(user.uid)
        user.nodes should contain only nid
        users.foreach { uid =>
          User(uid).nodes should contain(nid)
        }
        publisher.tags should contain theSameElementsAs tags
    }
  }

  it should "not accept users outside entourage" in {

    val user = User.create("email@email.com")
    val meta = Metadata.now(user.uid)

    val users = List[UID](4, 5, 6)
    val action = new PublisherCreate("name", users, List.empty, List.empty)

    val thrown = the[UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include(user.uid.toString)

  }

}
