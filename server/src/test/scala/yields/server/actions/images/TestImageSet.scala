package yields.server.actions.images

import org.scalacheck.Arbitrary._
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Test class for ImageSet action
  *
  * TODO implement test
  */
class TestImageSet extends FlatSpec with Matchers with BeforeAndAfter {

  val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)
  val contentTest = "this is an impossible content for a normal image so it can be used for testing and it can be deleted after each test"

  "set a valid image" should "set it in database and on disk" in {
    val action = ImageSet(contentTest)
    val res = action.run(m)
    res match {
      case ImageSetRes(x) =>
        val img = Image(x)
        img.nid should be(x)
        img.content should be(contentTest)
    }
  }

}
