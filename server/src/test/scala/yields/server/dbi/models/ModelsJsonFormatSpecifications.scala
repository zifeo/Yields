package yields.server.dbi.models

import spray.json._
import yields.server.io._
import org.scalacheck.{Prop, Properties}

object ModelsJsonFormatSpecifications extends Properties("ModelsJsonFormat") with ModelsGenerators {

  import Prop.forAll

}