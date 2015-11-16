package yields.server.actions

import org.scalacheck.Arbitrary
import yields.server._
import yields.server.actions.groups._
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

  implicit lazy val groupMessageArb: Arbitrary[GroupMessage] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      content <- arbitrary[String]
    } yield GroupMessage(nid, content)
  }

  implicit lazy val groupMessageResArb: Arbitrary[GroupMessageRes] = Arbitrary {
    GroupMessageRes()
  }

  implicit lazy val groupHistoryArb: Arbitrary[GroupHistory] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      lastTid <- arbitrary[TID]
      count <- arbitrary[Int]
    } yield GroupHistory(nid, lastTid, count)
  }

  implicit lazy val groupHistoryResArb: Arbitrary[GroupHistoryRes] = Arbitrary {
    for {
      nodes <- arbitrary[List[FeedContent]]
    } yield GroupHistoryRes(nodes)
  }

}
