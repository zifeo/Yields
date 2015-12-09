package yields.server.actions.nodes

import yields.server.dbi.models.{Node, Publisher}
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec

class TestNodeSearch extends YieldsSpec {

  "NodeSearch" should "find some results with tags" in {

    val tags = List("hello", "4242", "To be or not to be")
    val pub = Publisher.create("test", 1)
    pub.addTags(tags)

    val meta = Metadata.now(0)
    val actions = tags.map(NodeSearch)

    actions.zip(tags).foreach { case (action, tag) =>
      action.run(meta) match {
        case NodeSearchRes(nids, names, pics) =>
          nids.foreach { nid =>
            Publisher(nid).tags should contain(tag)
          }
      }
    }
  }

  it should "find some results with partial tags" in {
    val tags = List("hello", "4242", "To be or not to be")
    val pub = Publisher.create("test", 1)
    pub.addTags(tags)

    val meta = Metadata.now(0)
    val actions = tags.map(tag => NodeSearch(tag.drop(2)))

    actions.zip(tags).foreach { case (action, tag) =>
      action.run(meta) match {
        case NodeSearchRes(nids, names, pics) =>
          nids.foreach { nid =>
            Publisher(nid).tags should contain(tag)
          }
      }
    }
  }

  it should "find some results with names" in {
    val names = List("hello", "4242", "To be or not to be")
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

  it should "find some results with partial names" in {
    val names = List("hello", "4242", "To be or not to be")
    val nodes = names.map(Publisher.create(_, 1))

    val meta = Metadata.now(0)
    val actions = names.map(name => NodeSearch(name.dropRight(2)))

    actions.zip(nodes).foreach { case (action, node) =>
      action.run(meta) match {
        case NodeSearchRes(nids, names, pics) =>
          nids should contain (node.nid)
          names should contain (node.name)
          pics should contain (node.pic)
      }
    }
  }

}
