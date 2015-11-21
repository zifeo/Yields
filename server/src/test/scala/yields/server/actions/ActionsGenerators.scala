package yields.server.actions

import org.scalacheck.{Arbitrary, Gen}

trait ActionsGenerators extends GroupsGenerators with UsersGenerators {

  import Gen.oneOf

  implicit lazy val actionArb: Arbitrary[Action] = Arbitrary {
    val arbs = Seq(
      groupCreateArb, groupUpdateArb, groupMessageArb, groupHistoryArb, groupSearchArb,
      userConnectArb, userUpdateArb, userGroupListArb, userInfoArb)
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

  implicit lazy val resultArb: Arbitrary[Result] = Arbitrary {
    val arbs = Seq(
      groupCreateResArb, groupUpdateResArb, groupMessageResArb, groupHistoryResArb, groupSearchResArb,
      userConnectResArb, userUpdateResArb, userGroupListResArb, userInfoResArb)
    assert(arbs.size >= 2)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

}
