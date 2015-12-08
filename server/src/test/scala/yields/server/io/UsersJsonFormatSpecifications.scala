package yields.server.io

import yields.server.actions.users._
import yields.server.tests._

class UsersJsonFormatSpecifications extends YieldsPropsSpec {

  property("UserConnect") {
    forAll() { (x: UserConnect) =>
      checkToAndFromJson(x)
    }
  }

  property("UserConnectRes") {
    forAll() { (x: UserConnectRes) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("UserUpdate") {
    forAll() { (x: UserUpdate) =>
      checkToAndFromJson(x)
    }
  }

  property("UserUpdateRes") {
    forAll() { (x: UserUpdateRes) =>
      checkToAndFromJson(x)
    }
  }

  property("UserUpdateBrd") {
    forAll() { (x: UserUpdateBrd) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("UserGroupList") {
    forAll() { (x: UserNodeList) =>
      checkToAndFromJson(x)
    }
  }

  property("UserGroupListRes") {
    forAll() { (x: UserNodeListRes) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("UserInfo") {
    forAll() { (x: UserNodeListRes) =>
      checkToAndFromJson(x)
    }
  }

  property("UserInfoRes") {
    forAll() { (x: UserNodeListRes) =>
      checkToAndFromJson(x)
    }
  }

  //

  property("UserSearch") {
    forAll() { (x: UserSearch) =>
      checkToAndFromJson(x)
    }
  }

  property("UserSearchRes") {
    forAll() { (x: UserSearchRes) =>
      checkToAndFromJson(x)
    }
  }

}