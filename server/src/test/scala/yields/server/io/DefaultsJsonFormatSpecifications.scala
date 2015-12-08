package yields.server.io

import java.time.OffsetDateTime

import yields.server.tests._

class DefaultsJsonFormatSpecifications extends YieldsPropsSpec {

  property("OffsetDateTime") {
    forAll() { (x: OffsetDateTime) =>
      checkToAndFromJson(x)
    }
  }

}
