package yields.server.actions.users

import org.scalatest.Matchers
import yields.server._
import yields.server.actions.exceptions.UnauthorizedActionException
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestUserInfo extends DBFlatSpec with Matchers {

  "UserInfo" should "get client info" in {

    val user = User.create("e1@email.com")
    val u2 = User.create("e2@email.com")
    val u3 = User.create("e3@email.com")
    val u4 = User.create("e4@email.com")
    val users = List(u2.uid, u3.uid, u4.uid)

    user.name = "name"
    user.addEntourage(users)
    val action = UserInfo(user.uid)
    val meta = Metadata.now(user.uid)

    action.run(meta) match {
      case UserInfoRes(uid, name, email, entourage, entourageUpdates) =>
        uid should be (user.uid)
        name should be (user.name)
        email should be (user.email)
        entourage should contain theSameElementsAs user.entourage
    }
  }

  it should "get entourage info only if the contact added him" in {

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

  it should "get only name if sharing at least one node with him" in {

    val user = User.create("e1@email.com")
    val contact = User.create("e2@email.com")
    contact.name = "name"

    val group = Group.create("share", user.uid)
    group.addUser(contact.uid)
    user.addNode(group.nid)
    contact.addNode(group.nid)

    val action = UserInfo(contact.uid)
    val meta = Metadata.now(user.uid)

    action.run(meta) match {
      case UserInfoRes(uid, name, email, entourage, entourageUpdates) =>
        uid should be (contact.uid)
        name should be (contact.name)
        email should be (empty)
        entourage should be (empty)
        entourageUpdates should be (empty)
    }
  }

  it should "not access info if the contact did not add him" in {

    val user = User.create("e1@email.com")
    val otherUid = user.uid + 1
    val action = UserInfo(otherUid)
    val meta = Metadata.now(user.uid)

    val thrown = the [UnauthorizedActionException] thrownBy action.run(meta)
    thrown.getMessage should include (otherUid.toString)

  }

}
