package yields.server.dbi.models

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server._

trait ModelsGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val groupArb: Arbitrary[Group] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield Group(nid)
  }

  implicit lazy val userArb: Arbitrary[User] = Arbitrary {
    for {
      uid <- arbitrary[UID]
    } yield User(uid)
  }

  implicit lazy val feedContentArb: Arbitrary[FeedContent] = Arbitrary {
    for {
      datetime <- arbitrary[OffsetDateTime]
      uid <- arbitrary[UID]
      text <- arbitrary[String]
    } yield (datetime, uid, None, text)
  }

}
