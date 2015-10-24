package yields.server.actions

import org.scalacheck.Arbitrary
import yields.server._
import yields.server.actions.users._
import yields.server.models._

trait UsersGenerators extends ModelsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val userConnectArb: Arbitrary[UserConnect] = Arbitrary {
    for {
      email <- cleanStringGen
    } yield UserConnect(email)
  }

  implicit lazy val userConnectResArb: Arbitrary[UserConnectRes] = Arbitrary {
    for {
      uid <- arbitrary[UID]
    } yield UserConnectRes(uid)
  }

  implicit lazy val userUpdateArb: Arbitrary[UserUpdate] = Arbitrary {
    for {
      uid <- arbitrary[UID]
      email <- cleanOptionStringGen
      name <- cleanOptionStringGen
      image <- arbitrary[Option[Blob]]
    } yield UserUpdate(uid, email, name, image)
  }

  implicit lazy val userUpdateResArb: Arbitrary[UserUpdateRes] = Arbitrary {
    UserUpdateRes()
  }

  implicit lazy val userGroupListArb: Arbitrary[UserGroupList] = Arbitrary {
    for {
      uid <- arbitrary[UID]
    } yield UserGroupList(uid)
  }

  implicit lazy val userGroupListResArb: Arbitrary[UserGroupListRes] = Arbitrary {
    for {
      groups <- arbitrary[List[Group]]
    } yield UserGroupListRes(groups)
  }

}
