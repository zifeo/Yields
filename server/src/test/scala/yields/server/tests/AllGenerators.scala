package yields.server.tests

import org.scalacheck.Arbitrary
import yields.server.actions.{PublishersGenerators, ActionsGenerators, GroupsGenerators, UsersGenerators}
import yields.server.mpi.MessagesGenerators

trait AllGenerators
  extends DefaultsGenerators
  with MessagesGenerators
  with ActionsGenerators
  with GroupsGenerators
  with UsersGenerators
  with PublishersGenerators {

  /** Sample an element of some type. */
  def sample[T](implicit a: Arbitrary[T]): T =
    Arbitrary.arbitrary[T].sample.get

}