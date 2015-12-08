package yields.server.actions

import yields.server.tests.YieldsSpec

class ActionsTest extends YieldsSpec {

  "validEmail" should "accept basic emails" in {
    val emails = List(
      "test@test.ch",
      "test@test.ch.cooking",
      "test.test@test.ch",
      "100@100.ch"
    )
    emails.forall(validEmail) should be(true)

  }

  it should "reject malformed emails" in {
    val emails = List(
      "testtest.ch",
      "test@testch",
      "test @test.ch",
      "test@ test.ch",
      "test@test. ch"
    )
    emails.forall(validEmail) should be(false)
  }

  "validURL" should "accept basic urls" in {
    val urls = List(
      "https://www.google.com",
      "https://somethingis.cooking"
    )
    urls.forall(validURL) should be(true)
  }

  it should "reject malformed urls" in {
    val urls = List(
      "hello.com",
      "www.google.com",
      "www .google.com",
      "https://www.google .com"
    )
    urls.forall(validURL) should be(false)
  }

}
