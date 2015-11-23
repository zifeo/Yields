package yields.server.actions

import java.time.OffsetDateTime
import org.scalacheck._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import yields.server.DefaultsGenerators
import yields.server.AllGenerators
import yields.server.actions.nodes.{NodeMessageRes, NodeHistoryRes, NodeMessage, NodeHistory}
import yields.server.dbi.models._
import java.time.OffsetDateTime
import yields.server._
import com.redis.serialization.Parse.Implicits._

trait NodesGenerators extends AllGenerators {
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
      text <- sample[Option[String]]
      contentType <- sample[Option[String]]
      content <- sample[Option[Blob]]
    } yield NodeMessage(nid, text, contentType, content)
  }

  implicit lazy val nodeHistoryResArb: Arbitrary[NodeHistoryRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      nodes <- arbitrary[List[ResponseFeedContent]]
    } yield NodeHistoryRes(nid, nodes)
  }

  implicit lazy val nodeMessageResArb: Arbitrary[NodeMessageRes] = Arbitrary {
    for {
      date <- arbitrary[OffsetDateTime]
    } yield NodeMessageRes(date)
  }
}
