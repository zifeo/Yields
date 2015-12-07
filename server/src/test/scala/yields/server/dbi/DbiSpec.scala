package yields.server.dbi

import org.scalatest.{BeforeAndAfter, FlatSpecLike}

/**
  * Flush the database before and after each tests.
  * Database number is already changed (see application.conf in test folder).
  */
private[server] trait DbiSpec extends FlatSpecLike with BeforeAndAfter {

  before {
    redis(_.flushdb)
  }

  after {
    redis(_.flushdb)
  }

}
