package yields.server.utils

import java.util.Date

import org.scalacheck.{Prop, Properties}
import yields.server.DefaultsGenerators

object TemporalSpecifications extends Properties("TemporalUtils") with DefaultsGenerators {

  import Prop.forAll
  import Temporal._

  property("OffsetDateTime") = forAll { (x: Date) =>
    offsetDateTime2Date(date2OffsetDateTime(x)) == x
  }

}