package yields.server.actions.publisher

import org.scalatest.Matchers
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.actions.groups.GroupMessage
import yields.server.actions.nodes.{NodeHistory, NodeHistoryRes}
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.tests.AllGenerators
import yields.server.utils.Temporal

class TestPublisherMessage extends DBFlatSpec with Matchers with AllGenerators {

  "a group with publisher" should "receive messages from user and publisher" in {
    val user1 = User.create("email1@email.com")
    val user2 = User.create("email2@email.com")
    val userPublisher = User.create("email3@email.com")
    val publisher = Publisher.create("9gag", userPublisher.uid)

    val group = Group.create("let's talk about 9gag", user1.uid)
    group.addUser(user2.uid)
    group.addNode(publisher.nid)
    publisher.addNode(group.nid)

    val msg1Action = GroupMessage(group.nid, Some("hey"), None, None)
    val meta1 = Metadata.now(user1.uid)
    msg1Action.run(meta1)

    val publish1Action = PublisherMessage(publisher.nid, Some("Welcome in 9gag's feed"), None, None)
    val meta2 = Metadata.now(userPublisher.uid)
    publish1Action.run(meta2)

    val msg2Action = GroupMessage(group.nid, Some("Received 9gag's message ?"), None, None)
    val meta3 = Metadata.now(user2.uid)
    msg2Action.run(meta3)

    val groupHistoryAction = NodeHistory(group.nid, Temporal.now, 3)
    val meta4 = Metadata.now(user2.uid)
    groupHistoryAction.run(meta4) match {
      case NodeHistoryRes(nid, dates, senders, texts, _, _, _) =>
        nid should be(group.nid)
        dates.length should be(3)
        senders should contain theSameElementsInOrderAs List(meta1.client, publisher.nid, meta3.client)
        texts should contain theSameElementsInOrderAs List("hey", "Welcome in 9gag's feed", "Received 9gag's message ?")
    }
  }

  it should "not be allowed to a non-registered user to publish in a publisher" in {
    val user1 = User.create("email@email.com")
    val user2 = User.create("email2@email.com")
    PublisherCreate("name", List(), List(), List()).run(Metadata.now(user1.uid)) match {
      case PublisherCreateRes(nid) =>
        val action = PublisherMessage(nid, Some("some text"), None, None)
        an[UnauthorizedActionException] should be thrownBy action.run(Metadata.now(user2.uid))
    }

  }

}
