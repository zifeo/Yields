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
class TestImage extends FlatSpec with Matchers with BeforeAndAfter {

  val contentTest = "this is an impossible content for a normal image so it can be used for testing and it can be deleted after each test"
  val pathForContentTest = Image.buildPathFromName(Image.createHash(contentTest))

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

  "a created image" should "exists on disk" in {
    val img = Image.createImage(contentTest)
    println(img.path)
    val exists = Image.checkFileExist(img.hash)
    exists should be(true)
  }

  "file path" should "respect pattern storage/images/hash.bin" in {
    val img = Image.createImage(contentTest)
    val hash = img.hash
    val path = Image.buildPathFromName(hash)
    path should be(img.path)
  }

  "creating an image with some content" should "return the image with the same content" in {
    val img = Image.createImage(contentTest)

    val img2 = Image(img.nid)

    img2.content should be(img.content)
  }

}