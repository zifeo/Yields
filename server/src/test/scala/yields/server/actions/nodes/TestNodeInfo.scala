package yields.server.actions.nodes

import yields.server.actions.publisher.PublisherInfoRes
import yields.server.actions.rss.RSSInfoRes
import yields.server.dbi.models._
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec

class TestNodeInfo extends YieldsSpec {

  "NodeInfo" should "return the correct result" in {

    val meta = Metadata.now(0)
    val publisher = Publisher.create("name", meta.client)
    val rss = RSS.create("name", "", "")

    NodeInfo(publisher.nid).run(meta) should be (a [PublisherInfoRes])
    NodeInfo(rss.nid).run(meta) should be (a [RSSInfoRes])

  }

}
