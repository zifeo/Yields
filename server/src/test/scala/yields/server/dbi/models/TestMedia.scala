package yields.server.dbi.models

import java.io.File

import yields.server.tests.YieldsSpec
import yields.server.utils.Temporal

/**
  * Test class for image model.
  */
class TestMedia extends YieldsSpec {

  val contentTypeTest = "image"
  val contentTest =
    """
      |this is an impossible content for a normal image so it can be used for testing and
      |it can be deleted after each test
    """.stripMargin

  val pathForContentTest = Media.buildPathFromName(Media.createHash(contentTest, Temporal.now))

  it should "exists on disk" in {
    val img = Media.create(contentTypeTest, contentTest, 1)
    val exists = Media.checkFileExist(Media.buildPathFromName(img.filename))
    exists should be(true)

  }

  it should "return the image with the same content" in {
    val img = Media.create(contentTypeTest, contentTest, 1)
    val img2 = Media(img.nid)

    img2.content should be(img.content)

  }

  "saving a file on disk" should "have the same content" in {
    val content = "Some content"
    val contentType = "image"
    val mediaBefore = Media.create(contentType, content, 0)
    val mediaAfter = Media(mediaBefore.nid)
    mediaBefore.content should be(content)
    mediaAfter.content should be(content)
    mediaBefore.contentType should be(contentType)
    mediaAfter.contentType should be(contentType)
    mediaBefore.creator should be(0)
    mediaAfter.creator should be(0)

  }
}
