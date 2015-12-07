package yields.server.io

import yields.server.actions.{Action, Broadcast, Result}
import yields.server.tests._

class ActionsJsonFormatSpecifications extends YieldsPropsSpec {

  property("Action") {
    forAll() { (x: Action) =>
      checkToAndFromJson(x)
    }
  }

  property("Result") {
    forAll() { (x: Result) =>
      checkToAndFromJson(x)
    }
  }

  property("Broadcast") {
    forAll() { (x: Broadcast) =>
      checkToAndFromJson(x)
    }
  }

}