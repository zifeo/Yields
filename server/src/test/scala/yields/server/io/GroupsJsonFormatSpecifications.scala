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
    toAndFromJson(x)
  }

  property("GroupCreateRes") = forAll { (x: GroupCreateRes) =>
    toAndFromJson(x)
  }

  property("GroupCreateBrd") = forAll { (x: GroupCreateBrd) =>
    toAndFromJson(x)
  }

  //

  property("GroupUpdate") = forAll { (x: GroupUpdate) =>
    toAndFromJson(x)
  }

  property("GroupUpdateRes") = forAll { (x: GroupUpdateRes) =>
    toAndFromJson(x)
  }

  property("GroupUpdateBrd") = forAll { (x: GroupUpdateBrd) =>
    toAndFromJson(x)
  }

  //

  property("GroupInfo") = forAll { (x: GroupInfo) =>
    toAndFromJson(x)
  }

  property("GroupInfoRes") = forAll { (x: GroupInfoRes) =>
    toAndFromJson(x)
  }

  //

  property("GroupMessage") = forAll { (x: GroupMessage) =>
    toAndFromJson(x)
  }

  property("GroupMessageRes") = forAll { (x: GroupMessageRes) =>
    toAndFromJson(x)
  }

  property("GroupMessageBrd") = forAll { (x: GroupMessageBrd) =>
    toAndFromJson(x)
  }

}