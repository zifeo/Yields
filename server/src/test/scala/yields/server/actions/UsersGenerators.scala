package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server.DefaultsGenerators
import yields.server.actions.users._
import yields.server.dbi.models._

trait UsersGenerators extends DefaultsGenerators with ModelsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val userConnectArb: Arbitrary[UserConnect] = Arbitrary {
    for {
      email <- arbitrary[String]
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
      email <- arbitrary[Option[String]]
      name <- arbitrary[Option[String]]
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
      groups <- arbitrary[List[(NID, String, OffsetDateTime)]]
    } yield UserGroupListRes(groups)
  }

  implicit lazy val userInfoArb: Arbitrary[UserInfo] = Arbitrary {
    for {
      uid <- arbitrary[UID]
    } yield UserInfo(uid)
  }

  implicit lazy val userInfoResArb: Arbitrary[UserInfoRes] = Arbitrary {
    for {
      uid <- arbitrary[UID]
      name <- arbitrary[String]
      email <- arbitrary[String]
      entourage <- arbitrary[Seq[UID]]
    } yield UserInfoRes(uid, name, email, entourage)
  }

}
