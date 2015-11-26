package yields.server.mpi

import java.time.OffsetDateTime

import org.scalacheck.Arbitrary
import yields.server.actions.{Action, ActionsGenerators, Result}
import yields.server.dbi.models.UID

trait MessagesGenerators extends ActionsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val requestArb: Arbitrary[Request] = Arbitrary {
    for {
      action <- arbitrary[Action]
      metadata <- arbitrary[Metadata]
    } yield Request(action, metadata)
  }

  implicit lazy val responseArb: Arbitrary[Response] = Arbitrary {
    for {
      result <- arbitrary[Result]
      metadata <- arbitrary[Metadata]
    } yield Response(result, metadata)
  }

  implicit lazy val metadataArb: Arbitrary[Metadata] = Arbitrary {
    for {
      uid <- arbitrary[UID]
      datetime <- arbitrary[OffsetDateTime]
      ref <- arbitrary[OffsetDateTime]
    } yield Metadata(uid, datetime, ref)
  }

}
