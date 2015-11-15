package yields.server.actions.groups

import org.scalacheck.Properties
import org.scalatest.{Matchers, FlatSpec, BeforeAndAfter}
import yields.server.actions.ActionsGenerators
import yields.server.dbi._
import yields.server.utils.Config

/**
  * Test class for Group search action
  * TODO implement test
  */
class TestGroupSearch extends FlatSpec with Matchers with BeforeAndAfter {

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

}
