package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server.actions.publisher._
import yields.server.dbi.models.{TID, Blob, UID, NID}
import yields.server.tests.DefaultsGenerators

trait PublishersGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val publisherCreateArb: Arbitrary[PublisherCreate] = Arbitrary {
    for {
      name <- arbitrary[String]
      nodes <- arbitrary[List[NID]]
      users <- arbitrary[List[UID]]
      tags <- arbitrary[List[String]]
    } yield PublisherCreate(name, users, nodes, tags)
  }

  implicit lazy val publisherCreateResArb: Arbitrary[PublisherCreateRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield PublisherCreateRes(nid)
  }

  implicit lazy val publisherCreateBrdArb: Arbitrary[PublisherCreateBrd] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[String]
      nodes <- arbitrary[List[NID]]
      users <- arbitrary[List[UID]]
    } yield PublisherCreateBrd(nid, name, users, nodes)
  }

  //

  implicit lazy val publisherUpdateArb: Arbitrary[PublisherUpdate] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[Option[String]]
      image <- arbitrary[Option[Blob]]
      addUser <- arbitrary[List[UID]]
      removeUser <- arbitrary[List[UID]]
      addNode <- arbitrary[List[NID]]
      removeNode <- arbitrary[List[NID]]
      addTags <- arbitrary[List[String]]
      removeTags <- arbitrary[List[String]]
    } yield PublisherUpdate(nid, name, image, addUser, removeUser, addNode, removeNode, addTags, removeTags)
  }

  implicit lazy val publisherUpdateResArb: Arbitrary[PublisherUpdateRes] = Arbitrary {
    PublisherUpdateRes()
  }

  implicit lazy val publisherUpdateBrdArb: Arbitrary[PublisherUpdateBrd] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[String]
      image <- arbitrary[Blob]
      users <- arbitrary[List[UID]]
      nodes <- arbitrary[List[NID]]
    } yield PublisherUpdateBrd(nid, name, image, users, nodes)
  }

  //

  implicit lazy val publisherInfoArb: Arbitrary[PublisherInfo] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield PublisherInfo(nid)
  }

  implicit lazy val publisherInfoResArb: Arbitrary[PublisherInfoRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[String]
      image <- arbitrary[Blob]
      users <- arbitrary[List[UID]]
      nodes <- arbitrary[List[NID]]
      tags <- arbitrary[Set[String]]
    } yield PublisherInfoRes(nid, name, image, users, nodes, tags)
  }

  //

  implicit lazy val publisherMessageArb: Arbitrary[PublisherMessage] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      text <- arbitrary[Option[String]]
      contentType <- arbitrary[Option[String]]
      content <- arbitrary[Option[Blob]]
    } yield PublisherMessage(nid, text, contentType, content)
  }

  implicit lazy val publisherMessageResArb: Arbitrary[PublisherMessageRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      datetime <- arbitrary[OffsetDateTime]
      contentNid <- arbitrary[Option[NID]]
    } yield PublisherMessageRes(nid, datetime, contentNid)
  }

}
