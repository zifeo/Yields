package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck._
import yields.server._
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeSearchRes, NodeSearch}
import yields.server.dbi.models._
import yields.server.tests.DefaultsGenerators

trait GroupsGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val groupCreateArb: Arbitrary[GroupCreate] = Arbitrary {
    for {
      name <- arbitrary[String]
      nodes <- arbitrary[List[NID]]
      users <- arbitrary[List[UID]]
    } yield GroupCreate(name, users, nodes)
  }

  implicit lazy val groupCreateResArb: Arbitrary[GroupCreateRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield GroupCreateRes(nid)
  }

  implicit lazy val groupCreateBrdArb: Arbitrary[GroupCreateBrd] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[String]
      nodes <- arbitrary[List[NID]]
      users <- arbitrary[List[UID]]
    } yield GroupCreateBrd(nid, name, users, nodes)
  }

  //

  implicit lazy val groupUpdateArb: Arbitrary[GroupUpdate] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[Option[String]]
      image <- arbitrary[Option[Blob]]
      addUser <- arbitrary[List[UID]]
      removeUser <- arbitrary[List[UID]]
      addNode <- arbitrary[List[NID]]
      removeNode <- arbitrary[List[NID]]
    } yield GroupUpdate(nid, name, image, addUser, removeUser, addNode, removeNode)
  }

  implicit lazy val groupUpdateResArb: Arbitrary[GroupUpdateRes] = Arbitrary {
    GroupUpdateRes()
  }

  implicit lazy val groupUpdateBrdArb: Arbitrary[GroupUpdateBrd] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[String]
      image <- arbitrary[Blob]
      users <- arbitrary[List[UID]]
      nodes <- arbitrary[List[NID]]
    } yield GroupUpdateBrd(nid, name, image, users, nodes)
  }

  //

  implicit lazy val groupInfoArb: Arbitrary[GroupInfo] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield GroupInfo(nid)
  }

  implicit lazy val groupInfoResArb: Arbitrary[GroupInfoRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[String]
      image <- arbitrary[Blob]
      users <- arbitrary[List[UID]]
      nodes <- arbitrary[List[NID]]
    } yield GroupInfoRes(nid, name, image, users, nodes)
  }

  //

  implicit lazy val groupMessageArb: Arbitrary[GroupMessage] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
    } yield GroupMessage(nid, text, contentType, content)
  }

  implicit lazy val groupMessageResArb: Arbitrary[GroupMessageRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetime <- arbitrary[OffsetDateTime]
      contentNid <- arbitrary[Option[NID]]
    } yield GroupMessageRes(nid, datetime, contentNid)
  }

}
