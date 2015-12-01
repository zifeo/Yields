package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.UsersGenerators
import yields.server.actions.users._

object UsersJsonFormatSpecifications extends Properties("UsersJsonFormat") with UsersGenerators {

  import Prop.forAll

  property("UserConnect") = forAll { (x: UserConnect) =>
    toAndFromJson(x)
  }

  property("UserConnectRes") = forAll { (x: UserConnectRes) =>
    toAndFromJson(x)
  }

  //

  property("UserUpdate") = forAll { (x: UserUpdate) =>
    toAndFromJson(x)
  }

  property("UserUpdateRes") = forAll { (x: UserUpdateRes) =>
    toAndFromJson(x)
  }

  property("UserUpdateBrd") = forAll { (x: UserUpdateBrd) =>
    toAndFromJson(x)
  }

  //

  property("UserGroupList") = forAll { (x: UserNodeList) =>
    toAndFromJson(x)
  }

  property("UserGroupListRes") = forAll { (x: UserNodeListRes) =>
    toAndFromJson(x)
  }

  //

  property("UserInfo") = forAll { (x: UserNodeListRes) =>
    toAndFromJson(x)
  }

  property("UserInfoRes") = forAll { (x: UserNodeListRes) =>
    toAndFromJson(x)
  }

  //

  property("UserSearch") = forAll { (x: UserSearch) =>
    toAndFromJson(x)
  }

  property("UserSearchRes") = forAll { (x: UserSearchRes) =>
    toAndFromJson(x)
  }

}