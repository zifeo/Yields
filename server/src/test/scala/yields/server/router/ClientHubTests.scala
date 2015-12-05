package yields.server.router

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.Tcp
import akka.stream.actor.{ActorPublisherMessage, ActorSubscriberMessage}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import yields.server.actions.users.{UserSearchRes, UserConnectRes, UserSearch}
import yields.server.mpi.{Response, Metadata, Request}
import yields.server.pipeline.blocks.SerializationModule
import yields.server.io._
import yields.server.tests.{AllGenerators, FakeActor}

class ClientHubTests(sys: ActorSystem)
  extends TestKit(sys) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll with AllGenerators {

  import ActorPublisherMessage._
  import ActorSubscriberMessage._
  import ClientHub._
  import Dispatcher._
  import SerializationModule._
  import Tcp._

  def this() = this(ActorSystem("Yields-tests"))

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  def setup: (TestActorRef[ClientHub], FakeActor, FakeActor) = {
    val fakeSocket = TestActorRef[FakeActor](FakeActor.props(self))
    val fakeDispatcher = TestActorRef[FakeActor](FakeActor.props(self))
    val hubProps = ClientHub.props(fakeSocket, InetSocketAddress.createUnresolved("", 0), fakeDispatcher)
    val hub = TestActorRef[ClientHub](hubProps)
    (hub, fakeSocket.underlying.actor.asInstanceOf[FakeActor], fakeDispatcher.underlying.actor.asInstanceOf[FakeActor])
  }

  "ClientHub" should "refuse answering until user is connected" in {

    val (hub, _, _) = setup
    val searchResponse = serialize(Response(UserSearchRes(0), Metadata.now(0)))
    val connectResponse = serialize(Response(UserConnectRes(0, returning = true), Metadata.now(0)))

    hub ! OnNext(searchResponse)
    expectMsg()
    println(socket.history)

  }

}
