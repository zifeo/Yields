package yields.server.actions

import org.scalacheck.{Arbitrary, Gen}

trait ActionsGenerators extends GroupsGenerators with UsersGenerators with NodesGenerators {

  import Gen.oneOf

  implicit lazy val actionArb: Arbitrary[Action] = Arbitrary {
    val arbs = Seq(
      groupCreateArb, groupUpdateArb, groupInfoArb, groupMessageArb,
      nodeHistoryArb, nodeSearchArb,
      userConnectArb, userUpdateArb, userInfoArb, userGroupListArb
    )
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

  implicit lazy val resultArb: Arbitrary[Result] = Arbitrary {
    val arbs = Seq(
      groupCreateResArb, groupUpdateResArb, groupInfoResArb, groupMessageResArb,
      nodeHistoryResArb, nodeSearchResArb,
      userConnectResArb, userUpdateResArb, userInfoResArb, userGroupListResArb
    )
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

  implicit lazy val broadcastArb: Arbitrary[Broadcast] = Arbitrary {
    val arbs = Seq(
      groupCreateBrdArb, groupUpdateBrdArb, groupMessageBrdArb,
      userUpdateBrdArb
    )
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

}
