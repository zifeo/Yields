package yields.server.actions

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server._
import yields.server.actions.groups.{GroupCreate, GroupHistory, GroupMessage, GroupUpdate}
import yields.server.models._

trait GroupsGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val groupCreateArb: Arbitrary[GroupCreate] = Arbitrary {
    for {
      name <- cleanStringGen
      nodes <- arbitrary[Seq[NID]]
    } yield GroupCreate(name, nodes)
  }

  implicit lazy val groupUpdateArb: Arbitrary[GroupUpdate] = Arbitrary {
    for {
      gid <- arbitrary[GID]
      name <- cleanOptionStringGen
      image <- arbitrary[Option[Blob]]
    } yield GroupUpdate(gid, name, image)
  }

  implicit lazy val groupMessageArb: Arbitrary[GroupMessage] = Arbitrary {
    for {
      gid <- arbitrary[GID]
      content <- cleanStringGen
    } yield GroupMessage(gid, content)
  }

  implicit lazy val groupHistoryArb: Arbitrary[GroupHistory] = Arbitrary {
    for {
      gid <- arbitrary[GID]
      from <- arbitrary[OffsetDateTime]
      to <- arbitrary[OffsetDateTime]
    } yield GroupHistory(gid, from, to)
  }

}
