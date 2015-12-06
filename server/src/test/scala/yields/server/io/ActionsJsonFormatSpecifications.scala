package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server.tests._
import yields.server.actions.{Broadcast, Result, Action, ActionsGenerators}

object ActionsJsonFormatSpecifications extends Properties("ActionsJsonFormat") with ActionsGenerators {

  import Prop.forAll

  property("Action") = forAll { (x: Action) =>
    toAndFromJson(x)
  }

  property("Result") = forAll { (x: Result) =>
    toAndFromJson(x)
  }

  property("Broadcast") = forAll { (x: Broadcast) =>
    toAndFromJson(x)
  }

}