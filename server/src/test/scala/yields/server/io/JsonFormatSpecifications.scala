package yields.server.io

import org.scalacheck.{Prop, Properties}
import spray.json._
import yields.server.actions.groups.{GroupCreate, GroupHistory, GroupMessage, GroupUpdate}
import yields.server.actions.users.{UserConnect, UserGroupList, UserUpdate}
import yields.server.actions.{Action, ActionGenerators}

object JsonFormatSpecifications extends Properties("CustomJsonFormat") with ActionGenerators {

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

  property("UserConnect") = forAll { (x: UserConnect) =>
    toAndFromJson(x) == x
  }

  property("UserUpdate") = forAll { (x: UserUpdate) =>
    toAndFromJson(x) == x
  }

  property("UserGroupList") = forAll { (x: UserGroupList) =>
    toAndFromJson(x) == x
  }

  property("Message") = forAll { (x: Action) =>
    toAndFromJson(x) == x
  }

}