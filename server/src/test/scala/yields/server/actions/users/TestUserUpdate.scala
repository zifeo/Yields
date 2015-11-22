package yields.server.actions.users

import org.scalatest.Matchers
import yields.server._
import yields.server.dbi._
import yields.server.dbi.models._
import yields.server.mpi.Metadata

/**
  * Test class for User Update
  */
class TestUserUpdate extends DBFlatSpec with Matchers with AllGenerators {

  lazy val m = sample[Metadata]

  it should "update everything in database" in {
    val u = User.create("email12344321@email.com")
    u.name = "name"
    val action = new UserUpdate(u.uid, Some("newemail@email.com"), Some("new name"), None)
    action.run(m)
    val u2 = User(u.uid)
    u2.email should be("newemail@email.com")
    u2.name should be("new name")
  }

}
