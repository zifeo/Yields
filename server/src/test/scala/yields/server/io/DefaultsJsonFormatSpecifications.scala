package yields.server.io

import java.time.OffsetDateTime

import org.scalacheck.{Prop, Properties}
import yields.server._

object DefaultsJsonFormatSpecifications extends Properties("DefaultsJsonFormat") with DefaultsGenerators {

  import Prop.forAll

  property("OffsetDateTime") = forAll { (x: OffsetDateTime) =>
    toAndFromJson(x) == x
  }

}