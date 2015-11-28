package yields.server.actions.users

import org.scalatest.Matchers
import yields.server._
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Test class for UserGetEntourage action
  */
class TestUserInfo extends DBFlatSpec with Matchers with AllGenerators {

  lazy val m = sample[Metadata]

  it should "get some infos" in {
    val u1 = User.create("e1@email.com")
    val u2 = User.create("e2@email.com")
    val u3 = User.create("e3@email.com")
    val u4 = User.create("e4@email.com")

    u1.name = "name"

    u1.addEntourage(u2.uid)
    u1.addEntourage(u3.uid)
    u1.addEntourage(u4.uid)

    val action = UserInfo(u1.uid)
    val res = action.run(m)

    res match {
      case UserInfoRes(uid, name, email, entourage, entourageUpdates) =>
        uid should be (u1.uid)
        name should be ("name")
        email should be ("e1@email.com")
        entourage.toSet should contain(u2.uid)
        entourage.toSet should contain(u3.uid)
        entourage.toSet should contain(u4.uid)
    }
  }


}
