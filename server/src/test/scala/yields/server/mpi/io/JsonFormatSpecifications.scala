package yields.server.mpi.io

import org.scalacheck.{Prop, Properties}
import spray.json._
import yields.server.mpi.exceptions.SerializationMessageException
import yields.server.mpi.groups.{GroupHistory, GroupUpdate, GroupCreate, GroupMessage}
import yields.server.mpi.Message
import yields.server.mpi.users.UserUpdate

object JsonFormatSpecifications extends Properties("CustomJsonFormat") with MessageGenerators {

  import Prop.forAll

  def toAndFromJson[T: JsonWriter: JsonReader](elem: T): T =
    elem.toJson.toString().parseJson.convertTo[T]

  property("GroupCreate") = forAll { (x: GroupCreate) =>
    toAndFromJson(x) == x
  }

  property("GroupUpdate") = forAll { (x: GroupUpdate) =>
    toAndFromJson(x) == x
  }

  property("GroupMessage") = forAll { (x: GroupMessage) =>
    toAndFromJson(x) == x
  }

  property("GroupHistory") = forAll { (x: GroupHistory) =>
    toAndFromJson(x) == x
  }

  property("UserUpdate") = forAll { (x: UserUpdate) =>
    toAndFromJson(x) == x
  }

  property("SerializationMessageException") = forAll { (x: SerializationMessageException) =>
    toAndFromJson(x) == x
  }

  property("Message") = forAll { (x: Message) =>
    toAndFromJson(x) == x
  }

}