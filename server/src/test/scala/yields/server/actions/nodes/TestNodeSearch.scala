package yields.server.actions.nodes

import yields.server.dbi.models.{Node, Publisher}
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec

class TestNodeSearch extends YieldsSpec {

  "NodeSearch" should "find some results with tags" in {

    val names = List("hello", "42", "To be or not to be")
    val nodes = names.map(Publisher.create(_, 1))

    val meta = Metadata.now(0)
    val actions = names.map(NodeSearch)

    actions.zip(nodes).foreach { case (action, node) =>

      action.run(meta) match {
        case NodeSearchRes(nids, names, pics) =>
          nids should contain (node.nid)
          names should contain (node.name)
          pics should contain (node.pic)
      }

    }

  }

  it should "find some results with partial tags" in {

  }

  it should "find some results with names" in {

  }

  it should "find some results with partial names" in {

  }

}
