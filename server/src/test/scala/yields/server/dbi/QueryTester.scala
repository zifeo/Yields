package yields.server.dbi

import org.scalatest._
import yields.server.dbi.models.{Group, User}

class QueryTester extends FlatSpec with Matchers {

  "Query createUser" should "return the user newly created" in {
    val u:User = User.createUser("Query Tester", "query@tester.com")

    u.name should be ("Query Tester")
    u.email should be ("query@tester.com")
  }

  "Query getUserByEmail " should "return the right user" in {
    val userCreated = User.createUser("John", "john@test.com")
    val u:User = User.getUserByEmail(userCreated.email)

    u.name should be (userCreated.name)
    u.email should be (userCreated.email)
    u.date_creation should be (userCreated.date_creation)
  }

  "Query getUserByRid" should "return the right user" in {
    val userCreated = User.createUser("Paul", "paul@test.com")
    val u:User = User.getUserByRid(userCreated.id)

    u.name should be (userCreated.name)
    u.email should be (userCreated.email)
    u.date_creation should be (userCreated.date_creation)
  }

  "Adding a user to groups and querying them" should "return the list of groups the user is in" in {
    val user = User.createUser("Bob", "bob@test.com")
    val g:Group = Group.createGroup("test1")
    val g1:Group = Group.createGroup("test2")
    val g2:Group = Group.createGroup("test3")

    User.addUserToGroup(user.id, g.id)
    User.addUserToGroup(user.id, g1.id)
    User.addUserToGroup(user.id, g2.id)

    val list = User.getGroupsFromUser(user.id)

    // TODO: Add test on group list
    list.length should be (3)
  }

  "Creating a group" should "return the group newly created" in {
    val name = "A group name"
    val group = Group.createGroup(name)

    group.group_name should be (name)
  }

  "Create a group and add users then query the users from the group" should "return the list of users" in {
    val group = Group.createGroup("Tennis for ever")
    val user1 = User.createUser("User1", "u1@test.com")
    val user2 = User.createUser("User2", "u2@test.com")
    val user3 = User.createUser("User3", "u3@test.com")

    User.addUserToGroup(user1.id, group.id)
    User.addUserToGroup(user2.id, group.id)
    User.addUserToGroup(user3.id, group.id)

    val list = Group.getUsersFromGroup(group.id)

    // TODO: Add test on user list
    list.length should be (3)
  }

}
