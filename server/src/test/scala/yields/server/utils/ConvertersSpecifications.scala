package yields.server.utils

import java.util.Date

import org.scalacheck.{Prop, Properties}
import yields.server.DefaultsGenerators

object ConvertersSpecifications extends Properties("UtilsConverters") with DefaultsGenerators {

  import Converters._
  import Prop.forAll

  property("OffsetDateTime") = forAll { (x: Date) =>
    offsetDateTime2Date(date2OffsetDateTime(x)) == x
  }

}