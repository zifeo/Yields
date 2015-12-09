package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck._
import yields.server.actions.media.{MediaMessage, MediaMessageRes}
import yields.server.dbi.models._
import yields.server.tests.DefaultsGenerators

trait MediaGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val mediaMessageArb: Arbitrary[MediaMessage] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
    } yield MediaMessage(nid, text, contentType, content)
  }

  implicit lazy val mediaMessageResArb: Arbitrary[MediaMessageRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetime <- arbitrary[OffsetDateTime]
    } yield MediaMessageRes(nid, datetime)
  }

}
