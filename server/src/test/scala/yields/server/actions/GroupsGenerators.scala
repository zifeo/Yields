package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck._
import yields.server._
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeSearchRes, NodeSearch}
import yields.server.dbi.models._

trait GroupsGenerators extends DefaultsGenerators with ModelsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val groupCreateArb: Arbitrary[GroupCreate] = Arbitrary {
    for {
      name <- arbitrary[String]
      nodes <- arbitrary[List[NID]]
      users <- arbitrary[List[UID]]
      tags <- arbitrary[List[String]]
      visibility: String <- Gen.oneOf("private", "public")
    } yield GroupCreate(name, nodes, users, tags, visibility)
  }

  implicit lazy val groupCreateResArb: Arbitrary[GroupCreateRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield GroupCreateRes(nid)
  }

  implicit lazy val groupUpdateArb: Arbitrary[GroupUpdate] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[Option[String]]
      image <- arbitrary[Option[Blob]]
    } yield GroupUpdate(nid, name, image)
  }

  implicit lazy val groupUpdateResArb: Arbitrary[GroupUpdateRes] = Arbitrary {
    GroupUpdateRes()
  }

  /*
  implicit lazy val nodeMessageArb: Arbitrary[NodeMessage] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
    } yield NodeMessage(nid, text, contentType, content)
  }

  implicit lazy val nodeMessageResArb: Arbitrary[NodeMessageRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetime <- arbitrary[OffsetDateTime]
    } yield NodeMessageRes(nid, datetime)
  }
   */

}
