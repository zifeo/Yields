package yields.server.io

import org.scalacheck.{Prop, Properties}
import spray.json.JsonFormat
import yields.server._
import yields.server.actions.GroupsGenerators
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeHistoryRes, NodeHistory, NodeMessageRes, NodeMessage}

object GroupsJsonFormatSpecifications extends Properties("GroupsJsonFormat") with GroupsGenerators {

  import Prop.forAll

  property("GroupCreate") = forAll { (x: GroupCreate) =>
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

  property("GroupSearch") = forAll { (x: GroupSearch) =>
    toAndFromJson(x) == x
  }

  property("GroupSearchRes") = forAll { (x: GroupSearchRes) =>
    toAndFromJson(x) == x
  }

}