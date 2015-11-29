package yields.server.actions.users

import org.scalatest._
import yields.server._
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestUserGroupList extends DBFlatSpec with Matchers {

  "UserGroupList" should "return empty list when the group list is empty" in {

    val meta = Metadata.now(0)
    val action = UserGroupList()

    action.run(meta) match {
      case UserGroupListRes(groups, updates, refreshes) =>
        groups should be (empty)
        updates should be (empty)
        refreshes should be (empty)
    }
  }

  it should "return the group list when it is not empty" in {

    val user = User.create("an@email.com")
    val meta = Metadata.now(user.uid)
    val group1 = Group.create("g1", user.uid)
    val group2 = Group.create("g2", user.uid)
    user.addGroup(group1.nid)
    user.addGroup(group2.nid)
    val action = UserGroupList()

    action.run(meta) match {
      case UserGroupListRes(groups, updates, refreshes) =>
        groups should have size 2
        updates should have size 2
        refreshes should have size 2
        groups should contain theSameElementsAs List(group1.nid, group2.nid)
    }
  }

}
