package yields.server.mpi

import yields.server.tests._
import yields.server.io._
import yields.server.tests.YieldsPropsSpec

class MessagesJsonFormatSpecifications extends YieldsPropsSpec {

  property("Request") {
    forAll() { (x: Request) =>
      checkToAndFromJson(x)
    }
  }

  property("Response") {
    forAll() { (x: Response) =>
      checkToAndFromJson(x)
    }
  }

  property("Notification") {
    forAll() { (x: Notification) =>
      checkToAndFromJson(x)
    }
  }

  property("Metadata") {
    forAll() { (x: Metadata) =>
      checkToAndFromJson(x)
    }
  }

}
