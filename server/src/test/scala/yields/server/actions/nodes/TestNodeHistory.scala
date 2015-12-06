package yields.server.actions.nodes

import org.scalatest.Matchers
import yields.server.actions.exceptions.{ActionArgumentException, UnauthorizedActionException}
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.tests.{YieldsSpec, AllGenerators}
import yields.server.utils.Temporal

class TestNodeHistory extends YieldsSpec with Matchers with AllGenerators {

  def sendMessage(nid: NID, number: Int): List[FeedContent] = {
    val group = Group(nid)
    for (i <- (0 to number).toList) yield {
      val message = sample[FeedContent]
      assert(group.addMessage(message))
      message
    }
  }

  "NodeHistory" should "return last n messages" in {

    val number = 20
    val user = 0
    val group = Group.create("name",user)
    val messages = sendMessage(group.nid, number)

    val kept = messages.sortBy(_._1).takeRight(number / 2)
    val meta = Metadata.now(user)
    val action = NodeHistory(group.nid, Temporal.maximum, number / 2)

    action.run(meta.replied) match {
      case NodeHistoryRes(nid, datetimes, senders, texts, contentTypes, contents, contentNids) =>
        val medias = kept.map(_._3.map(Media(_)))

        nid should be (group.nid)
        datetimes should contain theSameElementsInOrderAs kept.map(_._1)
        senders should contain theSameElementsInOrderAs kept.map(_._2)
        texts should contain theSameElementsInOrderAs kept.map(_._4)
        contentTypes should contain theSameElementsInOrderAs medias.map(_.map(_.contentType))
        contents should contain theSameElementsInOrderAs medias.map(_.map(_.content))
        contentNids should contain theSameElementsInOrderAs medias.map(_.map(_.nid))

    }

  }

  it should "not accept if user does not belong to the node" in {

    val meta = Metadata.now(0)
    val group = Group.create("name", meta.client)
    val action =  NodeHistory(group.nid, Temporal.now, 1)
    val otherMeta = Metadata.now(meta.client + 1)

    val thrown = the [UnauthorizedActionException] thrownBy action.run(otherMeta)
    thrown.getMessage should include (otherMeta.client.toString)

  }

  it should "not accept if negative count" in {

    val count = -5
    val meta = Metadata.now(0)
    val action =  NodeHistory(meta.client, Temporal.now, count)

    val thrown = the [ActionArgumentException] thrownBy action.run(meta)
    thrown.getMessage should include (count.toString)

  }

}
