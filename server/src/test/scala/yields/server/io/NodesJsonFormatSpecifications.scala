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

<<<<<<< 30ac8fc0ea9aff8f04363bd2b66a6449bfd0d139
  property("NodeMessageBrd") {
    forAll() { (x: NodeMessageBrd) =>
=======
  property("NodeInfo") {
    forAll() { (x: NodeInfo) =>
>>>>>>> add test for node info
      checkToAndFromJson(x)
    }
  }

<<<<<<< 30ac8fc0ea9aff8f04363bd2b66a6449bfd0d139

=======
>>>>>>> add test for node info
}