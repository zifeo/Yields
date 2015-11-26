package yields.server.dbi

import org.scalatest.{BeforeAndAfter, FlatSpec}

/** Flush the database before and after each tests.
  * Database number is already changed (see application.conf in test folder).
  */
trait DBFlatSpec extends FlatSpec with BeforeAndAfter {

  before {
    redis(_.flushdb)
  }

  after {
    redis(_.flushdb)
  }

}
