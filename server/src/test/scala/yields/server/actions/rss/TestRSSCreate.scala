package yields.server.actions.rss

import yields.server.dbi.models.RSS
import yields.server.mpi.Metadata
import yields.server.tests.YieldsSpec

class TestRSSCreate extends YieldsSpec {

  "RSSCreate" should "create a RSS" in {

    val meta = Metadata.now(0)
    val name = "testing rss"
    val url = "http://stackoverflow.com/feeds"
    val filter = "testing filter"

    val action = RSSCreate(name, url, filter, List.empty)
    action.run(meta) match {
      case RSSCreateRes(nid) =>
        val rss = RSS(nid)
        rss.name should be (name)
        rss.url should be (url)
        rss.filter should be (filter)
    }
  }

  it should "answer with nid 0 when no RSS found for given url" in {

    val meta = Metadata.now(0)
    val action = RSSCreate("testing", "", "", List.empty)

    action.run(meta) match {
      case RSSCreateRes(nid) =>
        nid should be (0)
    }
  }

}