package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.UsersGenerators
import yields.server.actions.users._

object UsersJsonFormatSpecifications extends Properties("UsersJsonFormat") with UsersGenerators {

  import Prop.forAll

  property("UserConnect") = forAll { (x: UserConnect) =>
    toAndFromJson(x) == x
  }

  property("UserConnectRes") = forAll { (x: UserConnectRes) =>
    toAndFromJson(x) == x
  }

  property("UserUpdate") = forAll { (x: UserUpdate) =>
    toAndFromJson(x) == x
  }

  property("UserUpdateRes") = forAll { (x: UserUpdateRes) =>
    toAndFromJson(x) == x
  }

  property("UserGroupList") = forAll { (x: UserGroupList) =>
    toAndFromJson(x) == x
  }

  property("UserGroupListRes") = forAll { (x: UserGroupListRes) =>
    toAndFromJson(x) == x
  }

  property("UserInfo") = forAll { (x: UserGroupListRes) =>
    toAndFromJson(x) == x
  }

  property("UserInfoRes") = forAll { (x: UserGroupListRes) =>
    toAndFromJson(x) == x
  }

}