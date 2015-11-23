package yields.server

import org.scalacheck.Arbitrary
import yields.server.actions.{UsersGenerators, GroupsGenerators, ActionsGenerators}
import yields.server.mpi.MessagesGenerators

trait AllGenerators
  extends DefaultsGenerators
  with MessagesGenerators
  with ActionsGenerators
  with GroupsGenerators
  with UsersGenerators {

  /** Sample an element of some type. */
  def sample[T](implicit a: Arbitrary[T]): T =
    Arbitrary.arbitrary[T].sample.get

}