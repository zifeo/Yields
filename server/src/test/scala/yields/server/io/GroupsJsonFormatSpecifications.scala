package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.GroupsGenerators
import yields.server.actions.groups._

object GroupsJsonFormatSpecifications extends Properties("GroupsJsonFormat") with GroupsGenerators {

  import Prop.forAll

  property("GroupCreate") = forAll { (x: NodeCreate) =>
    toAndFromJson(x) == x
  }

  property("GroupCreateRes") = forAll { (x: GroupCreateRes) =>
    toAndFromJson(x) == x
  }

  property("GroupUpdate") = forAll { (x: GroupUpdate) =>
    toAndFromJson(x) == x
  }

  property("GroupUpdateRes") = forAll { (x: GroupUpdateRes) =>
    toAndFromJson(x) == x
  }

  property("GroupMessage") = forAll { (x: GroupMessage) =>
    toAndFromJson(x) == x
  }

  property("GroupMessageRes") = forAll { (x: GroupMessageRes) =>
    toAndFromJson(x) == x
  }

  property("GroupHistory") = forAll { (x: GroupHistory) =>
    toAndFromJson(x) == x
  }

  property("GroupHistoryRes") = forAll { (x: GroupHistoryRes) =>
    toAndFromJson(x) == x
  }

}