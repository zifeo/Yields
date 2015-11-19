package yields.server.actions.images

import org.scalacheck.Arbitrary._
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Test class for ImageGet action
  *
  * TODO implement test
  */
class TestImageGet extends FlatSpec with Matchers with BeforeAndAfter {

  val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  "running ImageGet after created an image" should "return the content of this image" in {
    val contentTest = "this is an impossible content for a normal image so it can be used for testing and it can be deleted after each test"
    val set = ImageSet(contentTest)
    val resSet = set.run(m)
    resSet match {
      case ImageSetRes(x) =>
        println("nid : " + x)
        val get = ImageGet(x)
        val resGet = get.run(m)
        resGet match {
          case ImageGetRes(content) =>
            content should be(contentTest)
        }
    }
  }


}
