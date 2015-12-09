package yields.server.rss

import yields.server.tests._
import yields.server.utils.Temporal

import scala.collection.JavaConverters._

class RSSFeedTests extends YieldsSpec {

  "A RSSFeed" should "correctly wrap a feed" in {

    val title = "title"
    val desc = "desc"
    val author = "author"
    val link = "link"
    val now = Temporal.now

    val romeFeed = createFeed(title, desc, author, link, now)
    val feed = new RSSFeed(romeFeed)

    feed.title should be (title)
    feed.description should be (desc)
    feed.author should be (author)
    feed.link should be (link)
    feed.updatedAt should be (now)
    feed.entries should be (empty)

  }

  it should "catch some updates" in {

    val title = "entry-title"
    val desc = "entry-desc"
    val author = "entry-author"
    val link = "entry-link"
    val now = Temporal.now

    val romeFeed = createFeed("title", "desc", "author", "link", Temporal.now)
    val romeEntry = createEntry(title, desc, author, link, now)
    romeFeed.setEntries(List(romeEntry, romeEntry).asJava)

    val feed = new RSSFeed(romeFeed)
    val feedEntry = RSSEntry(title, author, link, now, desc)

    feed.entries should have size 2
    feed.since(Temporal.minimum) should contain theSameElementsAs List(feedEntry, feedEntry)

  }

  it should "catch some partial updates" in {

    val title = "entry-title"
    val now = Temporal.now

    val romeFeed = createFeed("title", "desc", "author", "link", Temporal.now)
    val romeEntry = createEntry(title, null, null, null, now)
    romeFeed.setEntries(List(romeEntry, romeEntry).asJava)

    val feed = new RSSFeed(romeFeed)
    val feedEntry = RSSEntry(title, "", "", now, "")

    feed.entries should have size 2
    feed.since(Temporal.minimum) should contain theSameElementsAs List(feedEntry, feedEntry)

  }

  it should "catch only newest updates" in {

    val title = "entry-title"
    val desc = "entry-desc"
    val author = "entry-author"
    val link = "entry-link"
    val now = Temporal.now

    val romeFeed = createFeed("title", "desc", "author", "link", Temporal.now)
    val oldRomeEntry = createEntry(title, desc, author, link, now.minusHours(2))
    val newRomeEntry = createEntry(title, desc, author, link, now.plusHours(2))
    romeFeed.setEntries(List(oldRomeEntry, newRomeEntry).asJava)

    val feed = new RSSFeed(romeFeed)
    val feedEntry = RSSEntry(title, author, link, now.plusHours(2), desc)

    feed.entries should have size 2
    feed.since(now) should contain theSameElementsAs List(feedEntry)

  }

  it should "catch only filtered updates" in {

    val title = "entry-title"
    val desc1 = "fish-chicken"
    val desc2 = "chicken"
    val author = "entry-author"
    val link = "entry-link"
    val now = Temporal.now

    val romeFeed = createFeed("title", "desc", "author", "link", Temporal.now)
    val oldRomeEntry = createEntry(title, desc1, author, link, now)
    val newRomeEntry = createEntry(title, desc2, author, link, now)
    romeFeed.setEntries(List(oldRomeEntry, newRomeEntry).asJava)

    val feed = new RSSFeed(romeFeed)
    val feedEntry1 = RSSEntry(title, author, link, now, desc1)
    val feedEntry2 = feedEntry1.copy(content = desc2)

    feed.entries should have size 2
    feed.sinceFiltered(Temporal.minimum, "chicken") should contain theSameElementsAs List(feedEntry1, feedEntry2)
    feed.sinceFiltered(Temporal.minimum, "fish, chicken") should contain theSameElementsAs List(feedEntry1, feedEntry2)
    feed.sinceFiltered(Temporal.minimum, "fish") should contain theSameElementsAs List(feedEntry1)

  }

}
