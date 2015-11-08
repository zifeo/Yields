package yields.server.dbi.models

import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}

class TestUser extends FlatSpec with Matchers with BeforeAndAfter {

  before {
    User.flushDB
  }

  val email = "test@test.com"

  "A new user " should "have the correct email set" in {
    val u1 = User.create(email)
    val u2 = User(u1.uid)
    u2.hydrate()

    u2.email should be(email)
  }

  "A user with a name" should "have the correct name set" in {
    val name = "Test User"
    val u1 = User.create(email)
    u1.name_=(name)

    val u2 = User(u1.uid)
    u2.hydrate()

    u2.name should be(name)
  }

  "A user with a email set" should "have the correct email set" in {
    val newEmail = "test1@test.com"
    val u1 = User.create(email)
    u1.email_=(newEmail)
    val u2 = User(u1.uid)
    u2.hydrate()

    u2.email should be(newEmail)
  }

  "Getting an existing user from an email" should "return the correct user" in {
    val name = "Test User"
    val u1 = User.create(email)
    u1.name_=(name)

    val u2 = User.fromEmail(email)
    u2.email should be(email)
    u2.name should be(name)
  }


  "A user added to a group" should "have this group in his list" in {
    val u1 = User.create(email)
    u1.addToGroups(1234)
    val u2 = User(u1.uid)

    u2.groups should contain(1234)
  }

  "removing a group from a user" should "remove the group in the user" in {
    val u1 = User.create(email)
    u1.addToGroups(1234)
    val u2 = User(u1.uid)
    u2.removeFromGroups(1234)
    val u3 = User(u1.uid)

    u3.groups should not contain (1234)
  }

  "setting picture to a user" should "add the picture in the model" in {
    val u1 = User.create(email)
    val pictureAsString = "Actually we don't have any picture to put in our database"
    u1.picture_=(pictureAsString)
    val u2 = User(u1.uid)

    u2.picture should be(pictureAsString)
  }

  "adding a user in the entourage of one" should "add the user in the model" in {
    val u1 = User.create(email)
    val u2 = User.create("another@mail.com")

    u1.addToEntourage(u2.uid)

    val u3 = User(u1.uid)
    u3.entourage should contain(u2.uid)
  }

  "removing a user from another" should "remove it in the model" in {
    val u1 = User.create(email)
    val u2 = User.create("another@mail.com")
    u1.addToEntourage(u2.uid)
    val u3 = User(u1.uid)
    u3.removeFromEntourage(u2.uid)
    val u4 = User(u3.uid)

    u4.entourage should not contain (u2.uid)
  }

}
