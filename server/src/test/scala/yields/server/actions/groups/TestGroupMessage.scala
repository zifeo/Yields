package yields.server.actions.groups

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.nodes.NodeMessage
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

class TestGroupMessage extends DBFlatSpec with Matchers with AllGenerators {

  "GroupMessage" should "add new text message to the feed" in {

    val meta = Metadata.now(0)
    val text = "A message"

    val group = Group.create("GroupName", meta.client)
    val action = GroupMessage(group.nid, Some(text), None, None)

    action.run(meta) match {
      case GroupMessageRes(nid, datetime) =>
        val group = Group(nid)
        val feed = group.getMessagesInRange(Temporal.now, 10)

        feed should have size 1
        feed.head._1 should be (datetime)
        feed.head._2 should be (meta.client)
        feed.head._3 should be (None)
        feed.head._4 should be (text)
    }
  }

  it should "add new media message to the feed" in {

    val meta = Metadata.now(0)
    val contentType = "image"
    val content = "12"

    val group = Group.create("name", meta.client)
    val action = GroupMessage(group.nid, None, Some(contentType), Some(content))

    action.run(meta) match {
      case GroupMessageRes(nid, datetime) =>
        val group = Group(nid)
        val feed = group.getMessagesInRange(Temporal.now, 5)

        feed should have size 1
        feed.head._1 should be (datetime)
        feed.head._2 should be (meta.client)
        feed.head._3 should be (defined)
        feed.head._4 should be (empty)

        val media = Media(feed.head._3.get)
        Media.checkFileExist(media.hash) should be (true)
        media.contentType should be (contentType)
        media.content should be (content)
    }
  }

  it should "add new text and media message to the feed" in {

    val meta = Metadata.now(0)
    val text = "A message"
    val contentType = "image"
    val content = "12"

    val group = Group.create("name", meta.client)
    val action = GroupMessage(group.nid, Some(text), Some(contentType), Some(content))

    action.run(meta) match {
      case GroupMessageRes(nid, datetime) =>
        val group = Group(nid)
        val feed = group.getMessagesInRange(Temporal.now, 5)

        feed should have size 1
        feed.head._1 should be (datetime)
        feed.head._2 should be (meta.client)
        feed.head._3 should be (defined)
        feed.head._4 should be (text)

        val media = Media(feed.head._3.get)
        Media.checkFileExist(media.hash) should be (true)
        media.contentType should be (contentType)
        media.content should be (content)
    }
  }

  it should "not accept content type empty and full content and vice versa" in {

    val meta = Metadata.now(0)
    val group = Group.create("name", meta.client)
    val action = GroupMessage(group.nid, None, None, Some("12"))
    val actionInverse = GroupMessage(group.nid, None, Some("type"), None)

    the [ActionArgumentException] thrownBy action.run(meta)
    the [ActionArgumentException] thrownBy actionInverse.run(meta)

  }

  it should "not accept message from outside its users" in {

    val meta = Metadata.now(0)
    val group = Group.create("name", meta.client + 1)
    val action = GroupMessage(group.nid, Some("text"), None, None)

    val thrown = the [UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include (meta.client.toString)

  }

  it should "not accept message with nothing" in {

    val meta = Metadata.now(0)
    val group = Group.create("name", meta.client)
    val action = GroupMessage(group.nid, None, None, None)

    the [ActionArgumentException] thrownBy action.run(meta)

  }

}
