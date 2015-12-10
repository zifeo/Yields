package yields.server.actions

import org.scalacheck.{Arbitrary, Gen}

trait ActionsGenerators extends GroupsGenerators with UsersGenerators with NodesGenerators with MediaGenerators
with PublishersGenerators with RSSGenerators {

  import Gen.oneOf

  implicit lazy val actionArb: Arbitrary[Action] = Arbitrary {
    val arbs = Seq(
      groupCreateArb, groupUpdateArb, groupInfoArb, groupMessageArb,
      nodeHistoryArb, nodeSearchArb, nodeInfoArb,
      userConnectArb, userUpdateArb, userInfoArb, userGroupListArb,
      mediaMessageArb,
      publisherCreateArb, publisherInfoArb, publisherMessageArb, publisherUpdateArb,
      rssCreateArb, rssInfoArb
    )
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

  implicit lazy val resultArb: Arbitrary[Result] = Arbitrary {
    val arbs = Seq(
      groupCreateResArb, groupUpdateResArb, groupInfoResArb, groupMessageResArb,
      nodeHistoryResArb, nodeSearchResArb,
      userConnectResArb, userUpdateResArb, userInfoResArb, userGroupListResArb,
      mediaMessageResArb,
      publisherCreateResArb, publisherInfoResArb, publisherMessageResArb, publisherUpdateResArb,
      rssCreateResArb, rssInfoResArb
    )
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

  implicit lazy val broadcastArb: Arbitrary[Broadcast] = Arbitrary {
    val arbs = Seq(
      groupCreateBrdArb, groupUpdateBrdArb,
      userUpdateBrdArb,
      publisherCreateBrdArb, publisherUpdateBrdArb,
      nodeMessageBrdArb
    )
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

}
