package yields.server.actions.rss

import yields.server.dbi.models.RSS
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec

class TestRSSInfo extends YieldsSpec {

  "RSSInfo" should "get RSS info" in {

    val meta = Metadata.now(0)
    val rss = RSS.create("testing rss", "testing url", "testing fitler")
    val action = RSSInfo(rss.nid)

    action.run(meta) match {
      case RSSInfoRes(name, url, filter, _) =>
        name should be (rss.name)
        url should be (rss.url)
        filter should be (rss.filter)
    }
  }

}