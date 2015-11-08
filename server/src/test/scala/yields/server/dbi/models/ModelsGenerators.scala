package yields.server.dbi.models

import org.scalacheck.Arbitrary

trait ModelsGenerators {

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

}
