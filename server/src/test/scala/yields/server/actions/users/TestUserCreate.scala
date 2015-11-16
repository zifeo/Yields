package yields.server.actions.users

import org.scalacheck.Arbitrary._
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.actions.exceptions.{ActionArgumentException, NewUserExistException}
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

class TestUserCreate extends FlatSpec with Matchers with BeforeAndAfter {

  /** Switch on test database */
  before {
    redis.withClient(_.select(Config.getInt("test.database.id")))
    redis.withClient(_.flushdb)
  }

  /** Switch back on main database */
  after {
    redis.withClient(_.flushdb)
    redis.withClient(_.select(Config.getInt("database.id")))
  }

  lazy val m = new Metadata(arbitrary[UID].sample.getOrElse(1), Temporal.current)

  "Creating a new user with email and name" should "return it's uid" in {
    val action = new UserCreate("test@test.com", "name")
    val res = action.run(m)
    res match {
      case UserCreateRes(x: UID) => x.toInt should be > 0
    }
  }

  "Creating two users with the same email" should "throw an exception" in {
    val action1 = new UserCreate("test@test.com", "name1")
    val action2 = new UserCreate("test@test.com", "name2")
    action1.run(m)
    an[NewUserExistException] should be thrownBy action2.run(m)
  }

  "Creating a user with a bad email" should "throw an exception" in {
    val action1 = new UserCreate("test", "name1")
    an[ActionArgumentException] should be thrownBy action1.run(m)
  }

}
