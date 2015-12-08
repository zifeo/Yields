package yields.server.io

import yields.server.actions.publisher._
import yields.server.tests._

class PublishersJsonFormatSpecifications extends YieldsPropsSpec {

  property("PublisherCreate") {
    forAll() { (x: PublisherCreate) =>
      checkToAndFromJson(x)
    }
  }

  property("PublisherCreateRes") {
    forAll() { (x: PublisherCreateRes) =>
      checkToAndFromJson(x)
    }
  }

  property("PublisherCreateBrd") {
    forAll() { (x: PublisherCreateBrd) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("PublisherUpdate") {
    forAll() { (x: PublisherUpdate) =>
      checkToAndFromJson(x)
    }
  }

  property("PublisherUpdateRes") {
    forAll() { (x: PublisherUpdateRes) =>
      checkToAndFromJson(x)
    }
  }

  property("PublisherUpdateBrd") {
    forAll() { (x: PublisherUpdateBrd) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("PublisherInfo") {
    forAll() { (x: PublisherInfo) =>
      checkToAndFromJson(x)
    }
  }

  property("PublisherInfoRes") {
    forAll() { (x: PublisherInfoRes) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("PublisherMessage") {
    forAll() { (x: PublisherMessage) =>
      checkToAndFromJson(x)
    }
  }

  property("PublisherMessageRes") {
    forAll() { (x: PublisherMessageRes) =>
      checkToAndFromJson(x)
    }
  }

}