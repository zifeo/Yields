package yields.server.mpi.io

import spray.json._

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Properties}
import yields.server.mpi.{UserUpdate, GroupMessage}

object JsonFormatSpecifications extends Properties("CustomJsonFormat") {

  import Arbitrary.arbitrary

  implicit val groupMessageArb = Arbitrary {
    for {
      gid <- arbitrary[Long]
      context <- arbitrary[String]
    } yield GroupMessage(gid, context)
  }

  implicit val userUpdateArb = Arbitrary {
    for {
      uid <- arbitrary[Long]
      email <- arbitrary[Option[String]]
      name <- arbitrary[Option[String]]
      if email.isDefined || name.isDefined
    } yield UserUpdate(uid, email, name, None)
  }

  def toAndFromJson[T: JsonWriter: JsonReader](elem: T): T =
    elem.toJson.toString().parseJson.convertTo[T]

  property("GroupMessage") = forAll { (gm: GroupMessage) =>
    toAndFromJson(gm) == gm
  }

  property("UserUpdate") = forAll { (uu: UserUpdate) =>
    toAndFromJson(uu) == uu
  }

}