package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server.actions.PublishersGenerators
import yields.server.actions.publisher._
import yields.server._

object PublishersJsonFormatSpecifications extends Properties("PublishersJsonFormat") with PublishersGenerators {

  import Prop.forAll

  property("PublisherCreate") = forAll { (x: PublisherCreate) =>
    toAndFromJson(x)
  }

  property("PublisherCreateRes") = forAll { (x: PublisherCreateRes) =>
    toAndFromJson(x)
  }

  property("PublisherCreateBrd") = forAll { (x: PublisherCreateBrd) =>
    toAndFromJson(x)
  }

  //

  property("PublisherUpdate") = forAll { (x: PublisherUpdate) =>
    toAndFromJson(x)
  }

  property("PublisherUpdateRes") = forAll { (x: PublisherUpdateRes) =>
    toAndFromJson(x)
  }

  property("PublisherUpdateBrd") = forAll { (x: PublisherUpdateBrd) =>
    toAndFromJson(x)
  }

  //

  property("PublisherInfo") = forAll { (x: PublisherInfo) =>
    toAndFromJson(x)
  }

  property("PublisherInfoRes") = forAll { (x: PublisherInfoRes) =>
    toAndFromJson(x)
  }

  //

  property("PublisherMessage") = forAll { (x: PublisherMessage) =>
    toAndFromJson(x)
  }

  property("PublisherMessageRes") = forAll { (x: PublisherMessageRes) =>
    toAndFromJson(x)
  }

  property("PublisherMessageBrd") = forAll { (x: PublisherMessageBrd) =>
    toAndFromJson(x)
  }

}