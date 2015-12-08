package yields.server.io

import yields.server.mpi.{Metadata, Notification, Request, Response}
import yields.server.tests.{YieldsPropsSpec, _}

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
