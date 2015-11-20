package yields.server.actions.users

import org.scalacheck.Arbitrary._
import org.scalatest.Matchers
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal


/**
  * Test class for UserGetEntourage action
  */
class TestUserInfo extends DBFlatSpec with Matchers {

  lazy val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  it should "get some infos" in {
    val u1 = User.create("e1@email.com")
    val u2 = User.create("e2@email.com")
    val u3 = User.create("e3@email.com")
    val u4 = User.create("e4@email.com")

    u1.name = "name"

    u1.addToEntourage(u2.uid)
    u1.addToEntourage(u3.uid)
    u1.addToEntourage(u4.uid)

    val action = new UserInfo(u1.uid)
    val res = action.run(m)

    res match {
      case UserInfoRes(name, email, entourage) =>
        name should be("name")
        email should be("e1@email.com")
        entourage.toSet should contain(u2.uid)
        entourage.toSet should contain(u3.uid)
        entourage.toSet should contain(u4.uid)
    }
  }


}
