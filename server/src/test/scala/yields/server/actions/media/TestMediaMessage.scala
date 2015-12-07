package yields.server.actions.media

import yields.server.actions.exceptions.{UnauthorizedActionException, ActionArgumentException}
import yields.server.actions.groups.{GroupMessageRes, GroupMessage}
import yields.server.dbi.models.{Media, Group}
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec
import yields.server.utils.Temporal

class TestMediaMessage extends YieldsSpec {

  "MediaMessage" should "add new text message to the comment feed" in {

    val meta = Metadata.now(0)
    val text = "A message"

    val media = Media.create("image", "Media content", meta.client)
    media.addUser(0)
    val action = MediaMessage(media.nid, Some(text), None, None)

    action.run(meta) match {
      case MediaMessageRes(nid, datetime) =>
        val m = Media(nid)

        val feed = m.getMessagesInRange(Temporal.now, 10)
        feed should have size 1
        feed.head._1 should be(datetime)
        feed.head._2 should be(meta.client)
        feed.head._3 should be(None)
        feed.head._4 should be(text)
    }
  }

  it should "add new text and media message to the feed" in {

    val meta = Metadata.now(0)
    val text = "A message"
    val contentType = "image"
    val content = "12"

    val media = Media.create("image", "Content", meta.client)
    media.addUser(0)
    val action = MediaMessage(media.nid, Some(text), Some(contentType), Some(content))

    action.run(meta) match {
      case MediaMessageRes(nid, datetime) =>
        val m = Media(nid)
        val feed = m.getMessagesInRange(Temporal.now, 5)

        feed should have size 1
        feed.head._1 should be(datetime)
        feed.head._2 should be(meta.client)
        feed.head._3 should be(defined)
        feed.head._4 should be(text)

        val media = Media(feed.head._3.get)
        Media.checkFileExist(Media.buildPathFromName(media.filename)) should be(true)
        media.contentType should be(contentType)
        media.content should be(content)
    }
  }

  it should "not accept content type empty and full content and vice versa" in {

    val meta = Metadata.now(0)
    val media = Media.create("image", "Content", meta.client)
    media.addUser(0)
    val action = MediaMessage(media.nid, None, None, Some("12"))
    val actionInverse = MediaMessage(media.nid, None, Some("type"), None)

    the[ActionArgumentException] thrownBy action.run(meta)
    the[ActionArgumentException] thrownBy actionInverse.run(meta)

  }

  it should "not accept message with nothing" in {

    val meta = Metadata.now(0)
    val media = Media.create("image", "Content", meta.client)
    media.addUser(0)
    val action = MediaMessage(media.nid, None, None, None)

    the[ActionArgumentException] thrownBy action.run(meta)

  }


}
