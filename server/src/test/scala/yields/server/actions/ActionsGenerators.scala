package yields.server.actions

import org.scalacheck.{Arbitrary, Gen}

trait ActionsGenerators extends GroupsGenerators with UsersGenerators with ExceptionsGenerators {

  import Gen.oneOf

  implicit lazy val actionArb: Arbitrary[Action] = Arbitrary {
    val arbs = Seq(groupCreateArb, groupUpdateArb, groupMessageArb, groupHistoryArb, userConnectArb, userUpdateArb,
      userGroupListArb)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

  implicit lazy val resultArb: Arbitrary[Result] = Arbitrary {
    val arbs = Seq(groupCreateResArb, groupUpdateResArb, groupMessageResArb, groupHistoryResArb, userConnectResArb,
      userUpdateResArb, userGroupListResArb)
    val gens = arbs.map(_.arbitrary)
    oneOf(gens.head, gens.tail.head, gens.drop(2): _*) // requires at least 2 brute entries
  }

}
