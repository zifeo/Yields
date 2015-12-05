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
      content <- arbitrary[List[Option[Blob]]]
    } yield NodeHistoryRes(nid, datetimes, senders, texts, contentTypes, content)
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

}
