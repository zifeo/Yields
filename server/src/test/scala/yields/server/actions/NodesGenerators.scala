package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server.DefaultsGenerators
import yields.server.actions.groups.{NodeMessageRes, NodeMessage}
import yields.server.actions.nodes.{NodeHistory, NodeHistoryRes}
import yields.server.dbi.models._

trait NodesGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val nodeHistoryArb: Arbitrary[NodeHistory] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      date <- arbitrary[OffsetDateTime]
      count <- arbitrary[Int]
    } yield NodeHistory(nid, date, count)
  }

  implicit lazy val nodeMessageArb: Arbitrary[NodeMessage] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
    } yield NodeMessage(nid, text, contentType, content)
  }

  implicit lazy val nodeHistoryResArb: Arbitrary[NodeHistoryRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetimes <- arbitrary[List[OffsetDateTime]]
      senders <- arbitrary[List[UID]]
      texts <- arbitrary[List[String]]
      contentTypes <- arbitrary[List[Option[String]]]
      content <- arbitrary[List[Option[Blob]]]
    } yield NodeHistoryRes(nid, datetimes, senders, texts, contentTypes, content)
  }

  implicit lazy val nodeMessageResArb: Arbitrary[NodeMessageRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetime <- arbitrary[OffsetDateTime]
    } yield NodeMessageRes(nid, datetime)
  }

}
