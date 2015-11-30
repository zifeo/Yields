package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.NodesGenerators
import yields.server.actions.nodes._

object NodesJsonFormatSpecifications extends Properties("NodesJsonFormat") with NodesGenerators {

  import Prop.forAll

  property("NodeHistory") = forAll { (x: NodeHistory) =>
    toAndFromJson(x)
  }

  property("NodeHistoryRes") = forAll { (x: NodeHistoryRes) =>
    toAndFromJson(x)
  }

  //

  property("NodeSearch") = forAll { (x: NodeSearch) =>
    toAndFromJson(x)
  }

  property("NodeSearchRes") = forAll { (x: NodeSearchRes) =>
    toAndFromJson(x)
  }

}