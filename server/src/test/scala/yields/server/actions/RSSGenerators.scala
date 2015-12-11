package yields.server.actions

import org.scalacheck.Arbitrary
import yields.server.actions.rss.{RSSInfoRes, RSSInfo, RSSCreateRes, RSSCreate}
import yields.server.dbi.models.NID
import yields.server.tests.DefaultsGenerators
import com.redis.serialization.Parse.Implicits._

trait RSSGenerators extends DefaultsGenerators {

  import Arbitrary.arbitrary

  implicit lazy val rssCreateArb: Arbitrary[RSSCreate] = Arbitrary {
    for {
      name <- arbitrary[String]
      url <- arbitrary[String]
      filter <- arbitrary[String]
      tags <- arbitrary[List[String]]
    } yield RSSCreate(name, url, filter, tags)
  }

  implicit lazy val rssCreateResArb: Arbitrary[RSSCreateRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield RSSCreateRes(nid)
  }

  //

  implicit lazy val rssInfoArb: Arbitrary[RSSInfo] = Arbitrary {
    for {
      nid <- arbitrary[NID]
    } yield RSSInfo(nid)
  }

  implicit lazy val rssInfoResArb: Arbitrary[RSSInfoRes] = Arbitrary {
    for {
      nid <- arbitrary[NID]
      name <- arbitrary[String]
      url <- arbitrary[String]
      filter <- arbitrary[String]
      tags <- arbitrary[Set[String]]
    } yield RSSInfoRes(nid, name, url, filter, tags)
  }

}
