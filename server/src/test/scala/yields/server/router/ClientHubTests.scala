package yields.server.router

import java.net.InetSocketAddress

import akka.io.Tcp
import akka.stream.actor.ActorSubscriberMessage
import akka.testkit.{TestActorRef, TestProbe}
import yields.server.actions.users.{UserConnectRes, UserSearchRes}
import yields.server.io._
import yields.server.mpi.{Metadata, Response}
import yields.server.pipeline.blocks.SerializationModule
import yields.server.tests.YieldsAkkaSpec

class ClientHubTests extends YieldsAkkaSpec {

  import ActorSubscriberMessage._
  import ClientHub._
  import Dispatcher._
  import SerializationModule._
  import Tcp._

  "ClientHub" should "refuse answering until user is connected" in {

    val socket = TestProbe()
    val dispatcher = TestProbe()
    val hubProps = ClientHub.props(socket.ref, InetSocketAddress.createUnresolved("", 0), dispatcher.ref)
    val hub = TestActorRef[ClientHub](hubProps)

    val searchResponse = serialize(Response(UserSearchRes(0), Metadata.now(0)))
    val connectResponse = serialize(Response(UserConnectRes(0, returning = true), Metadata.now(0)))
    val errorMessage = hub.underlyingActor.errorMessage

    hub ! OnNext(searchResponse)
    socket.expectMsg(Write(errorMessage, WriteAck(errorMessage)))
    hub ! WriteAck(errorMessage)

    hub ! OnNext(searchResponse)
    socket.expectMsg(Write(errorMessage, WriteAck(errorMessage)))
    hub ! WriteAck(errorMessage)

    hub ! OnNext(connectResponse)
    socket.expectMsg(Write(connectResponse, WriteAck(connectResponse)))
    dispatcher.expectMsg(InitConnection(0))
    hub ! WriteAck(connectResponse)

    hub ! OnNext(searchResponse)
    socket.expectMsg(Write(searchResponse, WriteAck(searchResponse)))

  }

}
