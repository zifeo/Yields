package yields.server.io

import yields.server.actions.nodes._
import yields.server.tests._

class NodesJsonFormatSpecifications extends YieldsPropsSpec {

  property("NodeHistory") {
    forAll() { (x: NodeHistory) =>
      checkToAndFromJson(x)
    }
  }

  property("NodeHistoryRes") {
    forAll() { (x: NodeHistoryRes) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("NodeSearch") {
    forAll() { (x: NodeSearch) =>
      checkToAndFromJson(x)
    }
  }

  property("NodeSearchRes") {
    forAll() { (x: NodeSearchRes) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("NodeMessageBrd") {
    forAll() { (x: NodeMessageBrd) =>
      checkToAndFromJson(x)
    }
  }


}