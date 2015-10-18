package yields.server.mpi.io

import org.scalacheck.{Gen, Arbitrary}
import yields.server.models.{DateTime, Blob, NID}
import yields.server.mpi.exceptions.SerializationMessageException
import yields.server.mpi.groups.{GroupHistory, GroupUpdate, GroupCreate, GroupMessage}
import yields.server.mpi.Message
import yields.server.mpi.users.UserUpdate

trait MessageGenerators {

  import Arbitrary.arbitrary
  import Gen.oneOf

  /** Avoids string to contain end-of-input char (waiting on spray-json/#137). */
  def avoidEOI(str: String): String = str.replace('\uFFFF', ' ')

  implicit lazy val groupCreateArb: Arbitrary[GroupCreate] = Arbitrary {
    for {
      name <- arbitrary[String]
      nodes <- arbitrary[Seq[NID]]
    } yield GroupCreate(avoidEOI(name), nodes)
  }

  implicit lazy val groupUpdateArb: Arbitrary[GroupUpdate] = Arbitrary {
    for {
      gid <- arbitrary[Long]
      name <- arbitrary[Option[String]]
      image <- arbitrary[Option[Blob]]
      if name.isDefined || image.isDefined
    } yield GroupUpdate(gid, name.map(avoidEOI), image)
  }

  implicit lazy val groupMessageArb: Arbitrary[GroupMessage] = Arbitrary {
    for {
      gid <- arbitrary[Long]
      context <- arbitrary[String]
    } yield GroupMessage(gid, avoidEOI(context))
  }

  implicit lazy val groupHistoryArb: Arbitrary[GroupHistory] = Arbitrary {
    for {
      gid <- arbitrary[Long]
      from <- arbitrary[DateTime]
      to <- arbitrary[DateTime]
    } yield GroupHistory(gid, from, to)
  }

  implicit lazy val userUpdateArb: Arbitrary[UserUpdate] = Arbitrary {
    for {
      uid <- arbitrary[Long]
      email <- arbitrary[Option[String]]
      name <- arbitrary[Option[String]]
      image <- arbitrary[Option[Blob]]
      if email.isDefined || name.isDefined || image.isDefined
    } yield UserUpdate(uid, email.map(avoidEOI), name.map(avoidEOI), image)
  }

  implicit lazy val serializationMessageExceptionArb: Arbitrary[SerializationMessageException] = Arbitrary {
    for {
      message <- arbitrary[String]
    } yield SerializationMessageException(avoidEOI(message))
  }

  implicit lazy val messageArb: Arbitrary[Message] = Arbitrary {
    val arbs = Seq(groupMessageArb, userUpdateArb, groupCreateArb)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

}
