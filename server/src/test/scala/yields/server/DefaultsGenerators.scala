package yields.server

import java.time.OffsetDateTime
import java.util.Date

import org.scalacheck.Arbitrary
import yields.server.utils.Temporal

trait DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val offsetDateTimeArb: Arbitrary[OffsetDateTime] = Arbitrary {
    for {
      date <- arbitrary[Date]
    } yield Temporal.date2OffsetDateTime(date)
  }

  // avoids finding end-of-input char
  implicit lazy val stringArb: Arbitrary[String] = Arbitrary {
    for {
      str <- arbitrary(Arbitrary.arbString)
    } yield str.replace('\uFFFF', ' ')
  }

}
