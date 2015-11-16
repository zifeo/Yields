package yields.server.actions.users

import org.scalacheck.Arbitrary._
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Temporal, Config}


/**
  * Test class for UserGetEntourage action
  */
class TestUserGetEntourage extends FlatSpec with Matchers with BeforeAndAfter {

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

  "an user with entourage" should "get some entourage" in {
    val u1 = User.create("e1@email.com")
    val u2 = User.create("e2@email.com")
    val u3 = User.create("e3@email.com")
    val u4 = User.create("e4@email.com")

    u1.addToEntourage(u2.uid)
    u1.addToEntourage(u3.uid)
    u1.addToEntourage(u4.uid)

    val action = new UserGetEntourage(u1.uid)
    val res = action.run(m)

    res match {
      case UserGetEntourageRes(x) =>
        x.toSet should contain(u2.uid)
        x.toSet should contain(u3.uid)
        x.toSet should contain(u4.uid)
    }
  }

  "an empty user entourage" should "return an empty sequence" in {
    val u1 = User.create("e1@email.com")
    val action = new UserGetEntourage(u1.uid)
    val res = action.run(m)

    res match {
      case UserGetEntourageRes(x) => x.isEmpty should be(true)
    }
  }

}
