package yields.server.actions.users

import org.scalatest.Matchers
import yields.server.AllGenerators
import yields.server.dbi.DBFlatSpec
import yields.server.dbi.models.User
import yields.server.mpi.Metadata

class TestUserSearch extends DBFlatSpec with Matchers {

  "UserSearch" should "find existing user" in {

    val user = User.create("e1@email.com")
    val otherUid = user.uid + 1

    val action = UserSearch(user.email)
    val meta = Metadata.now(otherUid)

    action.run(meta) match {
      case UserSearchRes(uid) =>
        uid should be (user.uid)
    }
  }

  it should "not find unexisting user" in {

    val action = UserSearch("test@email.com")
    val meta = Metadata.now(1)

    action.run(meta) match {
      case UserSearchRes(uid) =>
        uid should be (0)
    }
  }

}
