package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server.actions.nodes._
import yields.server.dbi.models._
import yields.server.tests.DefaultsGenerators

trait NodesGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val nodeHistoryArb: Arbitrary[NodeHistory] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      date <- arbitrary[OffsetDateTime]
      count <- arbitrary[Int]
    } yield NodeHistory(nid, date, count)
  }

  implicit lazy val nodeHistoryResArb: Arbitrary[NodeHistoryRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetimes <- arbitrary[List[OffsetDateTime]]
      senders <- arbitrary[List[UID]]
      texts <- arbitrary[List[String]]
      contentTypes <- arbitrary[List[Option[String]]]
      contents <- arbitrary[List[Option[Blob]]]
      contentNids <- arbitrary[List[Option[NID]]]
    } yield NodeHistoryRes(nid, datetimes, senders, texts, contentTypes, contents, contentNids)
  }

  //

  implicit lazy val nodeSearchArb: Arbitrary[NodeSearch] = Arbitrary {
    for {
      pattern <- arbitrary[String]
    } yield NodeSearch(pattern)
  }

  implicit lazy val nodeSearchResArb: Arbitrary[NodeSearchRes] = Arbitrary {
    for {
      nodes <- arbitrary[List[NID]]
      names <- arbitrary[List[String]]
      pics <- arbitrary[List[Blob]]
    } yield NodeSearchRes(nodes, names, pics)
  }

  //

  implicit lazy val nodeMessageBrdArb: Arbitrary[NodeMessageBrd] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetime <- arbitrary[OffsetDateTime]
      sender <- arbitrary[UID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
      contentNid <- arbitrary[Option[NID]]
    } yield NodeMessageBrd(nid, datetime, sender, text, contentType, content, contentNid)
  }

  //

  implicit lazy val nodeInfoArb: Arbitrary[NodeInfo] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield NodeInfo(nid)
  }

}
