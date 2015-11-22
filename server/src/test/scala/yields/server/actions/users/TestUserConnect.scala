package yields.server.actions.users

import org.scalacheck.Arbitrary._
import org.scalatest.Matchers
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Test class for User Connect action
  */
class TestUserConnect extends DBFlatSpec with Matchers {

  val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  "UserConnect" should "return the user if it exist in db" in {
    val email = "emai12763l@email.com"
    val u = User.create(email)
    val action = new UserConnect(email)
    val connectedUser = action.run(m)

    connectedUser match {
      case UserConnectRes(x: UID) =>
        x should be (u.uid)
        val user = User(x)
        user.email should be (email)
    }
  }

  it should "create the user if the user doesn't exist" in {
    val nonRegisteredEmail = "91b9cc47d30be9fb2e543201d78fb8f6@email.com"
    val action = new UserConnect(nonRegisteredEmail)
    val res = action.run(m)
    res match {
      case UserConnectRes(x) =>
        val user = User(x)
        user.email should be(nonRegisteredEmail)
        user.uid should be(x)
    }
  }

}
