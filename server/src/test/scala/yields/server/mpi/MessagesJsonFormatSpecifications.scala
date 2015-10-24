package yields.server.mpi

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.io._

object MessagesJsonFormatSpecifications extends Properties("MessagesJsonFormat") with MessagesGenerators {

  import Prop.forAll

  property("Request") = forAll { (x: Request) =>
    toAndFromJson(x) == x
  }

  property("Response") = forAll { (x: Response) =>
    toAndFromJson(x) == x
  }

  property("Metadata") = forAll { (x: Metadata) =>
    toAndFromJson(x) == x
  }


}