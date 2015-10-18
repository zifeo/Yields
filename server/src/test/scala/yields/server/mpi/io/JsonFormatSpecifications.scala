package yields.server.mpi.io

import org.scalacheck.{Prop, Properties}
import spray.json._
import yields.server.mpi.groups.GroupMessage
import yields.server.mpi.Message
import yields.server.mpi.users.UserUpdate

object JsonFormatSpecifications extends Properties("CustomJsonFormat") with MessageGenerators {

  import Prop.forAll

  def toAndFromJson[T: JsonWriter: JsonReader](elem: T): T =
    elem.toJson.toString().parseJson.convertTo[T]

  property("GroupMessage") = forAll { (gm: GroupMessage) =>
    toAndFromJson(gm) == gm
  }

  property("UserUpdate") = forAll { (uu: UserUpdate) =>
    toAndFromJson(uu) == uu
  }

  property("Message") = forAll { (m: Message) =>
    toAndFromJson(m) == m
  }

}