package yields.server.actions.groups

import org.scalacheck.Arbitrary._
import org.scalacheck.Properties
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.actions.ActionsGenerators
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.utils.{Config, Temporal}

/**
  * Test class for Group Update action
  * TODO updating picture
  */
class TestGroupUpdate extends FlatSpec with Matchers with BeforeAndAfter {

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

  "updating a group name" should "update the group in db" in {
    val g1 = Group.createGroup("name1")
    val action = new GroupUpdate(g1.nid, Some("name2"), None)
    action.run(m)
    val g2 = Group(g1.nid)
    g2.name should be("name2")
  }

}
