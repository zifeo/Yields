package yields.server.actions

import org.scalacheck.Arbitrary
import yields.server._
import yields.server.actions.users.{UserConnect, UserGroupList, UserUpdate}
import yields.server.models._

trait UsersGenerators {

  import Arbitrary.arbitrary

  implicit lazy val userConnectArb: Arbitrary[UserConnect] = Arbitrary {
    for {
      email <- cleanStringGen
    } yield UserConnect(email)
  }

  implicit lazy val userUpdateArb: Arbitrary[UserUpdate] = Arbitrary {
    for {
      uid <- arbitrary[UID]
      email <- cleanOptionStringGen
      name <- cleanOptionStringGen
      image <- arbitrary[Option[Blob]]
    } yield UserUpdate(uid, email, name, image)
  }

  implicit lazy val userGroupListArb: Arbitrary[UserGroupList] = Arbitrary {
    for {
      uid <- arbitrary[UID]
    } yield UserGroupList(uid)
  }

}
