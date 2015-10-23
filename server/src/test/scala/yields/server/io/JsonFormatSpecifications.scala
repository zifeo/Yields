package yields.server.io

import org.scalacheck.{Prop, Properties}
import spray.json._
import yields.server.actions.exceptions.SerializationActionException
import yields.server.actions.groups.{GroupHistory, GroupUpdate, GroupCreate, GroupAction}
import yields.server.actions.Action
import yields.server.actions.users.{UserGroupList, UserConnect, UserUpdate}

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

  property("GroupMessage") = forAll { (x: GroupAction) =>
    toAndFromJson(x) == x
  }

  property("GroupHistory") = forAll { (x: GroupHistory) =>
    toAndFromJson(x) == x
  }

  property("UserConnect") = forAll { (x: UserConnect) =>
    toAndFromJson(x) == x
  }

  property("UserUpdate") = forAll { (x: UserUpdate) =>
    toAndFromJson(x) == x
  }

  property("UserGroupList") = forAll { (x: UserGroupList) =>
    toAndFromJson(x) == x
  }

  property("SerializationMessageException") = forAll { (x: SerializationActionException) =>
    toAndFromJson(x) == x
  }

  property("Message") = forAll { (x: Action) =>
    toAndFromJson(x) == x
  }

}