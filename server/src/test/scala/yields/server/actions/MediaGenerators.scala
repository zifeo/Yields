package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import yields.server.actions.media.{MediaMessageBrd, MediaMessageRes, MediaMessage}
import yields.server.dbi.models._
import yields.server.tests.DefaultsGenerators
import com.redis.serialization.Parse.Implicits._

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

  implicit lazy val mediaMessageBrdArb: Arbitrary[MediaMessageBrd] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetime <- arbitrary[OffsetDateTime]
      sender <- arbitrary[UID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
      contentNid <- arbitrary[Option[NID]]
    } yield MediaMessageBrd(nid, datetime, sender, text, contentType, content)
  }

}
