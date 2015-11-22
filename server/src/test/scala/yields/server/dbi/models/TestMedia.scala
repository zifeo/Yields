package yields.server.dbi.models

import java.io.File
import java.nio.file.{Paths, Files}

import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.utils.Config

/**
  * Test class for image model
  *
  * TODO implement test
  */
class TestMedia extends FlatSpec with Matchers with BeforeAndAfter {

  val contentTypeTest = "Image"
  val contentTest = "this is an impossible content for a normal image so it can be used for testing and it can be deleted after each test"
  val pathForContentTest = Media.buildPathFromName(Media.createHash(contentTest))

  before {
    val file = new File(pathForContentTest)
    if (file.exists) {
      file.delete()
    }
  }

  after {
    val file = new File(pathForContentTest)
    if (file.exists) {
      file.delete()
    }
  }

  it should "exists on disk" in {
    val img = Media.createMedia(contentTypeTest, contentTest, 1)
    val exists = Media.checkFileExist(img.hash)
    exists should be(true)
  }

  "file path" should "respect pattern storage/media/hash.bin" in {
    val img = Media.createMedia(contentTypeTest, contentTest, 1)
    val hash = img.hash
    val path = Media.buildPathFromName(hash)
    path should be(img.path)
  }

  it should "return the image with the same content" in {
    val img = Media.createMedia(contentTypeTest, contentTest, 1)
    val img2 = Media(img.nid)

    img2.content should be(img.content)
  }

}
