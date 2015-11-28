package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.{NodesGenerators, GroupsGenerators}
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeHistory, NodeHistoryRes}

object NodesJsonFormatSpecifications extends Properties("NodesJsonFormat") with NodesGenerators {

  import Prop.forAll

  property("NodeMessage") = forAll { (x: NodeMessage) =>
    toAndFromJson(x) == x
  }

  property("NodeMessageRes") = forAll { (x: NodeMessageRes) =>
    toAndFromJson(x) == x
  }

  property("NodeHistory") = forAll { (x: NodeHistory) =>
    toAndFromJson(x) == x
  }

  property("NodeHistoryRes") = forAll { (x: NodeHistoryRes) =>
    toAndFromJson(x) == x
  }

}