package yields.server.utils

import java.util.Date

import yields.server.tests.YieldsPropsSpec
import yields.server.utils.Temporal._

class TemporalSpecifications extends YieldsPropsSpec {

  property("OffsetDateTime") {
    forAll() { (x: Date) =>
      offsetDateTime2Date(date2OffsetDateTime(x)) == x
    }
  }

}