package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server._
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeHistoryRes, NodeHistory, NodeMessageRes, NodeMessage}
import yields.server.dbi.models._

trait GroupsGenerators extends DefaultsGenerators with ModelsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val groupCreateArb: Arbitrary[GroupCreate] = Arbitrary {
    for {
      name <- arbitrary[String]
      nodes <- arbitrary[List[NID]]
      users <- arbitrary[List[UID]]
    } yield GroupCreate(name, nodes, users)
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

  implicit lazy val groupHistoryArb: Arbitrary[NodeHistory] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      date <- arbitrary[OffsetDateTime]
      count <- arbitrary[Int]
    } yield NodeHistory(nid, date, count)
  }

  implicit lazy val groupMessageArb: Arbitrary[NodeMessage] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
    } yield NodeMessage(nid, text, contentType, content)
  }

  implicit lazy val groupHistoryResArb: Arbitrary[NodeHistoryRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      nodes <- arbitrary[List[FeedContent]]
    } yield NodeHistoryRes(nid, nodes)
  }

  implicit lazy val groupMessageResArb: Arbitrary[NodeMessageRes] = Arbitrary {
    NodeMessageRes(true)
  }

}
