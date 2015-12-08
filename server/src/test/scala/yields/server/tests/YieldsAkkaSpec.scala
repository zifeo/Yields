package yields.server.tests

import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.Matchers
import yields.server.dbi.DbiSpec

/**
  * Include testing actor system.
  */
abstract class YieldsAkkaSpec extends TestKit(system) with ImplicitSender with Matchers with DbiSpec with AllGenerators
