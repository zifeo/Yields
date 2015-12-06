package yields.server.tests

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

/**
  * Property based testing.
  */
trait YieldsPropsSpec extends PropSpec with Matchers with GeneratorDrivenPropertyChecks with AllGenerators