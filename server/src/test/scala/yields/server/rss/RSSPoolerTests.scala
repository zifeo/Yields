package yields.server.rss

import akka.testkit.TestActorRef
import yields.server.dbi.models.RSS
import yields.server.tests.YieldsAkkaSpec
import yields.server.utils.Temporal

class RSSPoolerTests extends YieldsAkkaSpec {

  val stackoverflowFeed = "http://stackoverflow.com/feeds"

  "RSSPooler" should "should detect news updates" in {

    val pooler = TestActorRef[RSSPooler].underlyingActor
    pooler.updateRSS(List.empty) should be (empty)

    val stackFeed = RSS.create("stackoverflow", stackoverflowFeed, "")
    stackFeed.refreshedAt = Temporal.minimum
    pooler.updateRSS(List(stackFeed)) should not be empty

  }

}
