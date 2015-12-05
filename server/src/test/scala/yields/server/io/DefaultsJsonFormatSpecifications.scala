package yields.server.io

import java.time.OffsetDateTime

import org.scalacheck.{Prop, Properties}
import yields.server.tests._
import yields.server.tests.DefaultsGenerators

object DefaultsJsonFormatSpecifications extends Properties("DefaultsJsonFormat") with DefaultsGenerators {

  import Prop.forAll

  property("OffsetDateTime") = forAll { (x: OffsetDateTime) =>
    toAndFromJson(x)
  }

}