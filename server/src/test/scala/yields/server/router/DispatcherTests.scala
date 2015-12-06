package yields.server.router

import akka.actor.ActorSystem
import akka.testkit.{TestProbe, ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import yields.server.actions.groups.GroupMessageBrd
import yields.server.router.ClientHub.OnPush
import yields.server.tests.AllGenerators

class DispatcherTests(sys: ActorSystem)
  extends TestKit(sys) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll with AllGenerators {

  import Dispatcher._

  def this() = this(ActorSystem("Yields-tests"))

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Dispatcher" should "should manage push to one client on one connection" in {

    val dispatcher = TestActorRef[Dispatcher]
    val client = 0
    val broadcast = sample[GroupMessageBrd]

    dispatcher ! InitConnection(client)

    dispatcher ! Notify(List(client), broadcast)
    expectMsg(OnPush(broadcast))

    dispatcher ! TerminateConnection

    dispatcher ! Notify(List(client), broadcast)
    expectNoMsg()

  }

  it should "should manage push to two clients on one connection" in {

    val dispatcher = TestActorRef[Dispatcher]
    val probe = TestProbe()
    val clientA = 0
    val clientB = 1
    val broadcast = sample[GroupMessageBrd]

    dispatcher ! InitConnection(clientA)
    probe.send(dispatcher, InitConnection(clientB))

    dispatcher ! Notify(List(clientA, clientB), broadcast)
    expectMsg(OnPush(broadcast))
    probe.expectMsg(OnPush(broadcast))

  }

  it should "should manage push to one client on two connections" in {

    val dispatcher = TestActorRef[Dispatcher]
    val probe = TestProbe()
    val client = 0
    val broadcast = sample[GroupMessageBrd]

    dispatcher ! InitConnection(client)
    probe.send(dispatcher, InitConnection(client))

    dispatcher ! Notify(List(client), broadcast)
    expectMsg(OnPush(broadcast))
    probe.expectMsg(OnPush(broadcast))

  }

  it should "should manage push to only selected client" in {

    val dispatcher = TestActorRef[Dispatcher]
    val probe = TestProbe()
    val clientA = 0
    val clientB = 1
    val broadcast = sample[GroupMessageBrd]

    dispatcher ! InitConnection(clientA)
    probe.send(dispatcher, InitConnection(clientB))

    dispatcher ! Notify(List(clientB), broadcast)
    expectNoMsg()
    probe.expectMsg(OnPush(broadcast))

    dispatcher ! Notify(List(clientA), broadcast)
    expectMsg(OnPush(broadcast))
    probe.expectNoMsg()

  }

  it should "should manage push to client only when included in pool" in {

    val dispatcher = TestActorRef[Dispatcher]
    val client = 0
    val broadcast = sample[GroupMessageBrd]

    dispatcher ! InitConnection(client)

    dispatcher ! Notify(List(client), broadcast)
    expectMsg(OnPush(broadcast))

    dispatcher ! TerminateConnection

    dispatcher ! Notify(List(client), broadcast)
    expectNoMsg()

    dispatcher ! InitConnection(client)

    dispatcher ! Notify(List(client), broadcast)
    expectMsg(OnPush(broadcast))

  }

}
