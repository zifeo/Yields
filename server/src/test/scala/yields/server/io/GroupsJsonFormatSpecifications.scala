package yields.server.io

import org.scalacheck.{Prop, Properties}
import spray.json.JsonFormat
import yields.server._
import yields.server.actions.GroupsGenerators
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeSearchRes, NodeSearch, NodeHistoryRes, NodeHistory}

object GroupsJsonFormatSpecifications extends Properties("GroupsJsonFormat") with GroupsGenerators {

  import Prop.forAll

  property("GroupCreate") = forAll { (x: GroupCreate) =>
    toAndFromJson(x) == x
  }

  property("GroupCreateRes") = forAll { (x: GroupCreateRes) =>
    toAndFromJson(x) == x
  }

  property("GroupCreateBrd") = forAll { (x: GroupCreateBrd) =>
    toAndFromJson(x) == x
  }

  property("GroupUpdate") = forAll { (x: GroupUpdate) =>
    toAndFromJson(x) == x
  }

  property("GroupUpdateRes") = forAll { (x: GroupUpdateRes) =>
    toAndFromJson(x) == x
  }

  property("GroupUpdateBrd") = forAll { (x: GroupUpdateBrd) =>
    toAndFromJson(x) == x
  }

  property("GroupInfo") = forAll { (x: GroupInfo) =>
    toAndFromJson(x) == x
  }

  property("GroupInfoRes") = forAll { (x: GroupInfoRes) =>
    toAndFromJson(x) == x
  }

  property("GroupMessage") = forAll { (x: GroupMessage) =>
    toAndFromJson(x) == x
  }

  property("GroupMessageRes") = forAll { (x: GroupMessageRes) =>
    toAndFromJson(x) == x
  }

  property("GroupMessageBrd") = forAll { (x: GroupMessageBrd) =>
    toAndFromJson(x) == x
  }

}