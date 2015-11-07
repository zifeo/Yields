package yields.server.dbi.models

import java.util.Date

import org.scalacheck.Arbitrary
import yields.server._

trait ModelsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val groupArb: Arbitrary[Group] = Arbitrary {
    for {
      gid <- cleanStringGen
      name <- cleanStringGen
      lastActivity <- arbitrary[Date]
      g = new Group()
    } yield {
      g.id = gid
      g.group_name = name
      g.refreshed_at = lastActivity
      g
    }
  }

  implicit lazy val userArb: Arbitrary[User] = Arbitrary {
    for {
      uid <- cleanStringGen
      name <- cleanStringGen
      u = new User()
    } yield {
      u.id = uid
      u.name = name
      u
    }
  }

}
