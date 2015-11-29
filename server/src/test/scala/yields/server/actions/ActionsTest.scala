package yields.server.actions

import org.scalatest.{FlatSpec, Matchers}

class ActionsTest extends FlatSpec with Matchers {

  "validEmail" should "accept basic emails" in {
    val emails = List(
      "test@test.ch",
      "test@test.ch.cooking",
      "test.test@test.ch",
      "100@100.ch"
    )
    emails.forall(validEmail) should be (true)

  }

  it should "reject malformed emails" in {
    val emails = List(
      "testtest.ch",
      "test@testch",
      "test @test.ch",
      "test@ test.ch",
      "test@test. ch"
    )
    emails.forall(validEmail) should be (false)
  }

}
