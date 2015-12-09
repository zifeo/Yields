package yields.server.dbi.models

import yields.server.tests.YieldsSpec
import yields.server.utils.Temporal

class TestGroup extends YieldsSpec {

  val testName = "Group Test"

  "A group" should "insert the group in the database by creation" in {
    val g1 = Group.create(testName, 1)
    val g2 = Group(g1.nid)

    g2.nid should be(g1.nid)
    g2.name should be(g1.name)
    g2.kind should be(classOf[Group].getSimpleName)
  }

  "Adding user to a group" should "modify the model" in {
    val g1 = Group.create(testName, 1)
    val u1 = User.create("email")
    g1.addUser(u1.uid)
    val g2 = Group(g1.nid)

    g2.users should contain(u1.uid)
  }

  "Removing a user from a group" should "modify the model" in {
    val g1 = Group.create(testName, 1)
    val u1 = User.create("email")
    g1.addUser(u1.uid)
    val g2 = Group(g1.nid)
    g2.removeUser(u1.uid)
    val g3 = Group(g2.nid)

    g3.users should not contain u1.uid
  }

  "Adding multiple messages to a group" should "add the messages" in {
    val g1 = Group.create(testName, 1)
    val u1 = User.create("email")
    val u2 = User.create("other email")
    val m1 = (Temporal.now, u1.uid, None, "This is the body")
    val m2 = (Temporal.now, u2.uid, None, "other body")
    val m3 = (Temporal.now, u1.uid, None, "other other body")
    g1.addMessage(m1)
    g1.addMessage(m2)
    g1.addMessage(m3)
    val g2 = Group(g1.nid)
    val feed = g2.getMessagesInRange(Temporal.now, 3)

    feed.length should be(3)
    feed should contain(m1)
    feed should contain(m2)
    feed should contain(m3)
  }

  "Adding a node" should "add the node in the model" in {
    val g1 = Group.create(testName, 1)
    val g2 = Group.create("A chat group", 1)
    g1.addNode(g2.nid)
    val g3 = Group(g1.nid)

    g3.nodes should contain(g2.nid)
  }

  it should "update updated_at" in {
    val group = Group.create("name", 0)
    val upStart = group.updatedAt
    group.updated()
    val upEnd = group.updatedAt
    upStart should not be upEnd
  }

  it should "update refreshed_at" in {
    val group = Group.create("name", 0)
    val refStart = group.refreshedAt
    group.refreshed()
    val refEnd = group.refreshedAt
    refStart should not be refEnd
  }

}
