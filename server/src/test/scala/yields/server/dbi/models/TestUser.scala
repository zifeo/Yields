package yields.server.dbi.models

import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}

class TestUser extends FlatSpec with Matchers with BeforeAndAfter {

  before {
    User.flushDB
  }

  after {
    User.flushDB
  }

  "A new user " should "have the correct email set" in {
    val email = "test1@test.com"
    val u1 = User.create(email)
    val u2 = User(u1.uid)
    u2.hydrate()

    u2.email should be(email)
  }

  "A user with a name" should "have the correct name set" in {
    val email = "test2@test.com"
    val name = "Test User"
    val u1 = User.create(email)
    u1.name_=(name)

    val u2 = User(u1.uid)
    u2.hydrate()

    u2.name should be(name)
  }

  "A user with a email set" should "have the correct email set" in {
    val firstEmail = "test3@test.com"
    val newEmail = "test4@test.com"
    val u1 = User.create(firstEmail)
    u1.email_=(newEmail)
    val u2 = User(u1.uid)
    u2.hydrate()

    u2.email should be(newEmail)
  }

  "Getting an existing user from an email" should "return the correct user" in {
    val email = "test5@test.com"
    val name = "Test User"
    val u1 = User.create(email)
    u1.name_=(name)

    val u2 = User.fromEmail(email)
    u2.email should be(email)
    u2.name should be(name)
  }


  "A user added to a group" should "have this group in his list" in {
    val u1 = User.create("test6@test.com")
    u1.addToGroups(1234)
    val u2 = User(u1.uid)

    u2.groups should contain(1234)
  }

  "removing a group from a user" should "remove the group in the user" in {
    val u1 = User.create("test7@test.com")
    u1.addToGroups(1234)
    val u2 = User(u1.uid)
    u2.removeFromGroups(1234)
    val u3 = User(u1.uid)

    u3.groups should not contain(1234)
  }

}
