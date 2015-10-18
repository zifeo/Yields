package yields.server.mpi.io

import org.scalacheck.{Gen, Arbitrary}
import yields.server.models.NID
import yields.server.mpi.exceptions.SerializationMessageException
import yields.server.mpi.groups.{GroupCreate, GroupMessage}
import yields.server.mpi.Message
import yields.server.mpi.users.UserUpdate

trait MessageGenerators {

  import Arbitrary.arbitrary
  import Gen.oneOf

  implicit lazy val groupMessageArb: Arbitrary[GroupMessage] = Arbitrary {
    for {
      gid <- arbitrary[Long]
      context <- arbitrary[String]
    } yield GroupMessage(gid, context)
  }

  implicit lazy val groupCreateArb: Arbitrary[GroupCreate] = Arbitrary {
    for {
      name <- arbitrary[String]
      nodes <- arbitrary[Seq[NID]]
    } yield GroupCreate(name, nodes)
  }

  implicit lazy val userUpdateArb: Arbitrary[UserUpdate] = Arbitrary {
    for {
      uid <- arbitrary[Long]
      email <- arbitrary[Option[String]]
      name <- arbitrary[Option[String]]
      if email.isDefined || name.isDefined
    } yield UserUpdate(uid, email, name, None)
  }

  implicit lazy val serializationMessageExceptionArb: Arbitrary[SerializationMessageException] = Arbitrary {
    for {
      message <- arbitrary[String]
    } yield SerializationMessageException(message)
  }

  implicit lazy val messageArb: Arbitrary[Message] = Arbitrary {
    val arbs = Seq(groupMessageArb, userUpdateArb, groupCreateArb)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

}
