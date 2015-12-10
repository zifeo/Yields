package yields.server.io

import yields.server.actions.groups._
import yields.server.tests._

class GroupsJsonFormatSpecifications extends YieldsPropsSpec {

  property("GroupCreate") {
    forAll() { (x: GroupCreate) =>
      checkToAndFromJson(x)
    }
  }

  property("GroupCreateRes") {
    forAll() { (x: GroupCreateRes) =>
      checkToAndFromJson(x)
    }
  }

  property("GroupCreateBrd") {
    forAll() { (x: GroupCreateBrd) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("GroupUpdate") {
    forAll() { (x: GroupUpdate) =>
      checkToAndFromJson(x)
    }
  }

  property("GroupUpdateRes") {
    forAll() { (x: GroupUpdateRes) =>
      checkToAndFromJson(x)
    }
  }

  property("GroupUpdateBrd") {
    forAll() { (x: GroupUpdateBrd) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("GroupInfo") {
    forAll() { (x: GroupInfo) =>
      checkToAndFromJson(x)
    }
  }

  property("GroupInfoRes") {
    forAll() { (x: GroupInfoRes) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("GroupMessage") {
    forAll() { (x: GroupMessage) =>
      checkToAndFromJson(x)
    }
  }

  property("GroupMessageRes") {
    forAll() { (x: GroupMessageRes) =>
      checkToAndFromJson(x)
    }
  }

}