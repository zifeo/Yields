package yields.server.tests

import org.scalatest.{FlatSpec, Matchers}
import yields.server.dbi.DbiSpec

/**
  * Basic Yields testing specs.
  */
trait YieldsSpec extends FlatSpec with Matchers with DbiSpec with AllGenerators