package yields.server.actions.users

import org.scalatest.Matchers
import yields.server._
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestUserInfo extends DBFlatSpec with Matchers with AllGenerators {

  "UserInfo" should "get client infos" in {

    val user = User.create("e1@email.com")
    val u2 = User.create("e2@email.com")
    val u3 = User.create("e3@email.com")
    val u4 = User.create("e4@email.com")
    val users = List(u2.uid, u3.uid, u4.uid)

    user.name = "name"
    user.addMultipleUser(users)
    val action = UserInfo(user.uid)
    val meta = Metadata.now(user.uid)

    action.run(meta) match {
      case UserInfoRes(uid, name, email, entourage, entourageUpdates) =>
        uid should be (user.uid)
        name should be (user.name)
        email should be (user.email)
        entourage should contain theSameElementsAs users
    }
  }

  it should "get entourage infos only if the contact added him" in {

    val user = User.create("e1@email.com")
    val contact = User.create("e2@email.com")
    contact.name = "name"
    contact.addEntourage(user.uid)

    val action = UserInfo(contact.uid)
    val meta = Metadata.now(user.uid)

    action.run(meta) match {
      case UserInfoRes(uid, name, email, entourage, entourageUpdates) =>
        uid should be (contact.uid)
        name should be (contact.name)
        email should be (contact.email)
        entourage should be (empty)
        entourageUpdates should be (empty)
    }
  }

  it should "not access infos if the contact did not add him" in {

    val user = User.create("e1@email.com")
    val otherUid = user.uid + 1
    val action = UserInfo(otherUid)
    val meta = Metadata.now(user.uid)

    val thrown = the [UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include (otherUid.toString)

  }

}
