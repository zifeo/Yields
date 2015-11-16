package yields.server.actions.groups

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import yields.server.dbi._
import yields.server.utils.Config

/**
  * Test class for Group search action
  * TODO implement test
  */
class TestGroupSearch extends FlatSpec with Matchers with BeforeAndAfter {

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

}
