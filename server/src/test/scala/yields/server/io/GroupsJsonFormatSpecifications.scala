package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.GroupsGenerators
import yields.server.actions.groups.{GroupCreate, GroupHistory, GroupMessage, GroupUpdate}

object GroupsJsonFormatSpecifications extends Properties("GroupsJsonFormat") with GroupsGenerators {

  import Prop.forAll

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

}