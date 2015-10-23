package yields.server.io

import org.scalacheck.{Prop, Properties}
import yields.server._
import yields.server.actions.UsersGenerators
import yields.server.actions.users.{UserConnect, UserGroupList, UserUpdate}

object UsersJsonFormatSpecifications extends Properties("UsersJsonFormat") with UsersGenerators {

  import Prop.forAll

  property("UserConnect") = forAll { (x: UserConnect) =>
    toAndFromJson(x) == x
  }

  property("UserUpdate") = forAll { (x: UserUpdate) =>
    toAndFromJson(x) == x
  }

  property("UserGroupList") = forAll { (x: UserGroupList) =>
    toAndFromJson(x) == x
  }

}