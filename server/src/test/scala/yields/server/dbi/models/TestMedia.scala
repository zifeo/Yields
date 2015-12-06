package yields.server.dbi.models

import java.io.File
import java.nio.file.{Paths, Files}

import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.utils.{Temporal, Config}

/**
  * Test class for image model
  *
  * TODO implement test
  */
class TestMedia extends FlatSpec with Matchers with BeforeAndAfter {

  val contentTypeTest = "image"
  val contentTest =
    """
      |this is an impossible content for a normal image so it can be used for testing and
      |it can be deleted after each test
    """.stripMargin

  val pathForContentTest = Media.buildPathFromName(Media.createHash(contentTest, Temporal.now))

  it should "exists on disk" in {
    val img = Media.create(contentTypeTest, contentTest, 1)
    val exists = Media.checkFileExist(img.hash)
    exists should be(true)

    Media.deleteContentOnDisk(img.nid)
  }

  "file path" should "respect pattern storage/media/date_hash.bin" in {
    val img = Media.create(contentTypeTest, contentTest, 1)
    val hash = img.hash
    val path = Media.buildPathFromName(hash)
    path should be(img.path)

    Media.deleteContentOnDisk(img.nid)
  }

  it should "return the image with the same content" in {
    val img = Media.create(contentTypeTest, contentTest, 1)
    val img2 = Media(img.nid)

    img2.content should be(img.content)

    Media.deleteContentOnDisk(img.nid)
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

    Media.deleteContentOnDisk(mediaBefore.nid)
  }

}
