package yields.server.actions.users

import org.scalatest._
import yields.server._
import yields.server.actions.exceptions.ActionArgumentException
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

class TestUserConnect extends DBFlatSpec with Matchers {

  val meta = Metadata.now(0) // uid is not important at this point

  "UserConnect" should "return the user if it exist in db" in {

    val ref = User.create("emai12763l@email.com")
    val action = UserConnect(ref.email)

    action.run(meta) match {
      case UserConnectRes(uid, returning) =>
        val user = User(uid)
        user.uid should be (ref.uid)
        user.email should be (ref.email)
        returning should be (true)
    }
  }

  it should "create the user if the user doesn't exist" in {

    val nonRegisteredEmail = "91b9cc47d30be9fb2e543201d78fb8f6@email.com"
    val action = UserConnect(nonRegisteredEmail)

    action.run(meta) match {
      case UserConnectRes(uid, returning) =>
        val user = User(uid)
        user.email should be (nonRegisteredEmail)
        returning should be (false)
    }
  }

  it should "update the connection datetime after new connections" in {

    val ref = User.create("emai12763l@email.com")

    UserConnect(ref.email).run(meta)
    val firstConnection = User(ref.uid).connected_at
    UserConnect(ref.email).run(meta)
    val secondConnection = User(ref.uid).connected_at

    firstConnection should be <= secondConnection

  }

  it should "not run the action whenever there is a bad email" in {

    val badEmail = "bad@bad"
    val action = UserConnect(badEmail)

    val thrown = the [ActionArgumentException] thrownBy action.run(meta)
    thrown.getMessage should include (badEmail)

  }

}
