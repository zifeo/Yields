package yields.server.mpi.io

import org.scalacheck.{Gen, Arbitrary}
import yields.server.mpi.{Message, UserUpdate, GroupMessage}

trait MessageGenerators {

  import Arbitrary.arbitrary
  import Gen.oneOf

  implicit lazy val groupMessageArb: Arbitrary[GroupMessage] = Arbitrary {
    for {
      gid <- arbitrary[Long]
      context <- arbitrary[String]
    } yield GroupMessage(gid, context)
  }

  implicit lazy val userUpdateArb: Arbitrary[UserUpdate] = Arbitrary {
    for {
      uid <- arbitrary[Long]
      email <- arbitrary[Option[String]]
      name <- arbitrary[Option[String]]
      if email.isDefined || name.isDefined
    } yield UserUpdate(uid, email, name, None)
  }

  implicit lazy val messageArb: Arbitrary[Message] = Arbitrary {
    val arbs = Seq(groupMessageArb, userUpdateArb)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

}
