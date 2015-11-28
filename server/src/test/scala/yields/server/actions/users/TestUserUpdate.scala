package yields.server.actions.users

import org.scalatest.Matchers
import yields.server._
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestUserUpdate extends DBFlatSpec with Matchers {

  "UserUpdate" should "change only name" in {

    val start = User.create("email12344321@email.com")
    val meta = Metadata.now(start.uid)
    start.name = "name"

    val newName = "jacques"
    val action = UserUpdate(None, Some(newName), None, List.empty, List.empty)
    action.run(meta)

    val end = User(start.uid)
    end.email should be (start.email)
    end.name should be (newName)
    end.pic should be (start.pic)
    end.entourage should be (start.entourage)

  }

  it should "change only pic" in {

    val start = User.create("email12344321@email.com")
    val meta = Metadata.now(start.uid)
    start.pic = "photo"

    val newPic = "gif"
    val action = UserUpdate(None, None, Some(newPic), List.empty, List.empty)
    action.run(meta)

    val end = User(start.uid)
    end.email should be (start.email)
    end.name should be (start.name)
    end.pic should be (newPic)
    end.entourage should be (start.entourage)

  }

  it should "change only entourage" in {

    val start = User.create("email12344321@email.com")
    val meta = Metadata.now(start.uid)

    val newUsers = List[UID](2, 3, 4)
    val addAction = UserUpdate(None, None, None, newUsers, List.empty)
    addAction.run(meta)

    val middle = User(start.uid)
    middle.email should be (start.email)
    middle.name should be (start.name)
    middle.pic should be (start.pic)
    middle.entourage should be (newUsers)

    val oldUser = List[UID](3)
    val removeAction = UserUpdate(None, None, None, List.empty, oldUser)
    removeAction.run(meta)

    val end = User(start.uid)
    end.email should be (start.email)
    middle.name should be (start.name)
    end.pic should be (start.pic)
    end.entourage should be (newUsers.diff(oldUser))

  }

  it should "not change bad email" in {

    val start = User.create("email12344321@email.com")
    val meta = Metadata.now(start.uid)

    val badEmail = "jacques@oups"
    val action = UserUpdate(Some(badEmail), None, None, List.empty, List.empty)

    val thrown = the [ActionArgumentException] thrownBy action.run(meta)
    thrown.getMessage should include (badEmail)

  }

}
