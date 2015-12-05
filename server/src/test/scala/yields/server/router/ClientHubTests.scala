package yields.server.router

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.Tcp
import akka.stream.actor.{ActorPublisherMessage, ActorSubscriberMessage}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.ByteString
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import yields.server.actions.users.{UserSearchRes, UserConnectRes}
import yields.server.mpi.{Response, Metadata}
import yields.server.pipeline.blocks.SerializationModule
import yields.server.io._
import yields.server.tests.AllGenerators

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

  lazy val hub = {
    val hubProps = ClientHub.props(self, InetSocketAddress.createUnresolved("", 0), self)
    TestActorRef[ClientHub](hubProps)
  }

  "ClientHub" should "refuse answering until user is connected" in {

    val searchResponse = serialize(Response(UserSearchRes(0), Metadata.now(0)))
    val connectResponse = serialize(Response(UserConnectRes(0, returning = true), Metadata.now(0)))
    val errorMessage = ByteString(hub.underlyingActor.errorMessage)

    hub ! OnNext(searchResponse)
    expectMsg(Write(errorMessage, Ack(errorMessage)))
    hub ! Ack(errorMessage)

    hub ! OnNext(searchResponse)
    expectMsg(Write(errorMessage, Ack(errorMessage)))
    hub ! Ack(errorMessage)

    hub ! OnNext(connectResponse)
    expectMsgAllOf(
      Write(connectResponse, Ack(connectResponse)),
      InitConnection(0)
    )
    hub ! Ack(connectResponse)

    hub ! OnNext(searchResponse)
    expectMsg(Write(searchResponse, Ack(searchResponse)))

  }

}
