package yields.server.dbi.models

import org.scalatest.Matchers
import yields.server.dbi._

class TestUser extends DBFlatSpec with Matchers {

  val email = "test@test.com"

  "Creating and getting the newly created user from its uid" should "return the correct user" in {
    val u1 = User.create(email)
    val u2 = User(u1.uid)
    u2.email should be(email)
    u2.uid should be(u1.uid)
  }

  "Getting an existing user from an email" should "return the correct user" in {
    val name = "Test User"
    val u1 = User.create(email)
    u1.name = name

    val u2 = User.fromEmail(email).getOrElse(fail("no matching index"))
    u2.email should be(email)
    u2.name should be(name)
  }

  it should "have the correct email set" in {
    val u1 = User.create(email)
    val u2 = User(u1.uid)

    u2.email should be(email)
  }

  it should "have the correct name set" in {
    val name = "Test User"
    val u1 = User.create(email)
    u1.name = name

    val u2 = User(u1.uid)
    u2.name should be(name)
  }

  it should "have the correct email set when modifying the email" in {
    val newEmail = "test1@test.com"
    val u1 = User.create(email)
    u1.email = newEmail
    val u2 = User(u1.uid)

    u2.email should be(newEmail)
  }

  it should "have this group in his list" in {
    val u1 = User.create(email)
    u1.addGroup(1234)
    val u2 = User(u1.uid)

    u2.groups should contain(1234)
  }

  it should "remove the group in the user" in {
    val u1 = User.create(email)
    u1.addGroup(1234)
    val u2 = User(u1.uid)
    u2.removeGroups(1234)
    val u3 = User(u1.uid)

    u3.groups should not contain 1234
  }

  it should "add the picture in the model" in {
    val u1 = User.create(email)
    val pictureAsString = "Actually we don't have any picture to put in our database"
    u1.pic = pictureAsString
    val u2 = User(u1.uid)

    u2.pic should be(pictureAsString)
  }

  it should "add the user in the model" in {
    val u1 = User.create(email)
    val u2 = User.create("another@mail.com")

    u1.addEntourage(u2.uid)

    val u3 = User(u1.uid)
    u3.entourage should contain(u2.uid)
  }

  "removing a user from another" should "remove it in the model" in {
    val u1 = User.create(email)
    val u2 = User.create("another@mail.com")
    u1.addEntourage(u2.uid)
    val u3 = User(u1.uid)
    u3.removeEntourage(u2.uid)
    val u4 = User(u3.uid)

    u4.entourage should not contain u2.uid
  }

  "Trying to get a non-existent user" should "throw an exception" in {
    val user = User(1234567)
  }

}
