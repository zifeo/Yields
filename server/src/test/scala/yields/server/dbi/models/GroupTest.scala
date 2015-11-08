package yields.server.dbi.models

import java.time.OffsetDateTime

import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import yields.server.utils.Temporal

class GroupTest extends FlatSpec with Matchers with BeforeAndAfter {

  before {
    User.flushDB()
  }

  val testName = "Group Test"

  "Creating a group with a name" should "insert it in the database" in {
    val g1 = Group.createGroup(testName)
    val g2 = Group(g1.nid)

    g2.nid should be(g1.nid)
    g2.name should be(g1.name)
    g2.kind should be("Group")
  }

  "Adding user to a group" should "modify the model" in {
    val g1 = Group.createGroup(testName)
    val u1 = User.create("email")
    g1.addUser(u1.uid)
    val g2 = Group(g1.nid)

    g2.users should contain(u1.uid)
  }

  "Removing a user from a group" should "modify the model" in {
    val g1 = Group.createGroup(testName)
    val u1 = User.create("email")
    g1.addUser(u1.uid)
    val g2 = Group(g1.nid)
    g2.removeUser(u1.uid)
    val g3 = Group(g2.nid)

    g3.users should not contain (u1.uid)
  }

  "Adding multiple messages to a group" should "add the messages" in {
    val g1 = Group.createGroup(testName)
    val u1 = User.create("email")
    val u2 = User.create("other email")
    val m1: FeedContent = (Temporal.currentDatetime, u1.uid, None, "This is the body")
    val m2: FeedContent = (Temporal.currentDatetime, u2.uid, None, "other body")
    val m3: FeedContent = (Temporal.currentDatetime, u1.uid, None, "other other body")
    g1.addMessage(m1)
    g1.addMessage(m2)
    g1.addMessage(m3)
    val g2 = Group(g1.nid)
    val feed = g2.getMessagesInRange(0, 3)

    feed.length should be(3)
    feed should contain(m1)
    feed should contain(m2)
    feed should contain(m3)

  }

  "Adding a node" should "add the node in the model" in {
    val g1 = Group.createGroup(testName)
    val g2 = Group.createGroup("A chat group")
    g1.addNode(g2.nid)
    val g3 = Group(g1.nid)

    g3.node should contain(g2.nid)
  }


}
