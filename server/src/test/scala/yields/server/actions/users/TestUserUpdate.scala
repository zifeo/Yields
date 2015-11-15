package yields.server.actions.users

import org.scalacheck.Arbitrary._
import org.scalacheck.Properties
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.actions.ActionsGenerators
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

/**
  * Test class for User Update
  */
class TestUserUpdate extends FlatSpec with Matchers with BeforeAndAfter {

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

  "updating all values" should "update everything in database" in {
    val u = User.create("email12344321@email.com")
    u.name = "name"
    val action = new UserUpdate(u.uid, Some("newemail@email.com"), Some("new name"), None)
    action.run(m)
    val u2 = User(u.uid)
    u2.email should be("newemail@email.com")
    u2.name should be("new name")
  }

}
