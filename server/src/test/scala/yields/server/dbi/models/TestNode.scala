package yields.server.dbi.models

import yields.server.tests.YieldsSpec
import yields.server.utils.Temporal

class TestNode extends YieldsSpec {


  "A node" should "be searchable" in {

    val names = List("hello", "42", "To be or not to be")
    val nodes = names.map(Publisher.create(_, 1))

    names.zip(nodes).foreach { case (name, node) =>
      Node.fromName(name).map(_.nid) should contain(node.nid)
    }
  }

}
