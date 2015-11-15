package yields.server.actions.groups

import org.scalacheck.{Prop, Properties}
import yields.server.actions.ActionsGenerators

/**
  * Test if GroupCreate action performed well
  */
object TestGroupCreate extends Properties("GroupCreate") with ActionsGenerators {

  import Prop.forAll

  property("GroupCreateNameSet") = forAll { (a: GroupCreate) =>
    a.run()
  }

}
