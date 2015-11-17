package yields.server.actions.users

import org.scalacheck.Arbitrary._
import org.scalacheck.Properties
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.actions.ActionsGenerators
import yields.server.actions.exceptions.UnauthorizeActionException
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

/**
  * Test class for User Connect action
  */
class TestUserConnect extends FlatSpec with Matchers with BeforeAndAfter {

  /** Switch on test database */
  before {
    redis(_.select(Config.getInt("test.database.id")))
    redis(_.flushdb)
  }

  /** Switch back on main database */
  after {
    redis(_.flushdb)
    redis(_.select(Config.getInt("database.id")))
  }

  lazy val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  "connecting an existing user" should "return the user" in {
    val email = "emai12763l@email.com"
    val u = User.create(email)
    val action = new UserConnect(email)
    val connectedUser = action.run(m)

    connectedUser match {
      case UserConnectRes(x: UID) =>
        x should be(u.uid)
        val user = User(x)
        user.email should be(email)
    }
  }

  "connecting an non-existing user" should "throw an exception" in {
    val nonRegisteredEmail = "91b9cc47d30be9fb2e543201d78fb8f6@email.com"
    val action = new UserConnect(nonRegisteredEmail)
    an[UnauthorizeActionException] should be thrownBy action.run(m)
  }

}