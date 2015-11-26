package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.{Result, Action, ActionsGenerators}

object ActionsJsonFormatSpecifications extends Properties("ActionsJsonFormat") with ActionsGenerators {

  import Prop.forAll

  property("Action") = forAll { (x: Action) =>
    toAndFromJson(x) == x
  }

  property("Result") = forAll { (x: Result) =>
    toAndFromJson(x) == x
  }

}