package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server.actions.users._
import yields.server.dbi.models._
import yields.server.tests.DefaultsGenerators

trait UsersGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val userConnectArb: Arbitrary[UserConnect] = Arbitrary {
    for {
      email <- arbitrary[String]
    } yield UserConnect(email)
  }

  implicit lazy val userConnectResArb: Arbitrary[UserConnectRes] = Arbitrary {
    for {
      uid <- arbitrary[UID]
      returning <- arbitrary[Boolean]
    } yield UserConnectRes(uid, returning)
  }

  //

  implicit lazy val userUpdateArb: Arbitrary[UserUpdate] = Arbitrary {
    for {
      email <- arbitrary[Option[Email]]
      name <- arbitrary[Option[String]]
      image <- arbitrary[Option[Blob]]
      addEntourage <- arbitrary[List[UID]]
      removeEntourage <- arbitrary[List[UID]]
    } yield UserUpdate(email, name, image, addEntourage, removeEntourage)
  }

  implicit lazy val userUpdateResArb: Arbitrary[UserUpdateRes] = Arbitrary {
    UserUpdateRes()
  }

  implicit lazy val userUpdateBrdArb: Arbitrary[UserUpdateBrd] = Arbitrary {
    for {
      uid <- arbitrary[UID]
      email <- arbitrary[Email]
      name <- arbitrary[String]
      image <- arbitrary[Blob]
    } yield UserUpdateBrd(uid, email, name, image)
  }

  //

  implicit lazy val userGroupListArb: Arbitrary[UserNodeList] = Arbitrary {
    UserNodeList()
  }

  implicit lazy val userGroupListResArb: Arbitrary[UserNodeListRes] = Arbitrary {
    for {
      groups <- arbitrary[List[NID]]
      kind <- arbitrary[List[String]]
      updates <- arbitrary[List[OffsetDateTime]]
      refreshes <- arbitrary[List[OffsetDateTime]]
    } yield UserNodeListRes(groups, kind, updates, refreshes)
  }

  //

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
      pic <- arbitrary[Blob]
      entourage <- arbitrary[List[UID]]
      entourageUpdates <- arbitrary[List[OffsetDateTime]]
    } yield UserInfoRes(uid, name, email, pic, entourage, entourageUpdates)
  }

  //

  implicit lazy val userSearchArb: Arbitrary[UserSearch] = Arbitrary {
    for {
      email <- arbitrary[Email]
    } yield UserSearch(email)
  }

  implicit lazy val userSearchResArb: Arbitrary[UserSearchRes] = Arbitrary {
    for {
      uid <- arbitrary[UID]
    } yield UserSearchRes(uid)
  }

}
