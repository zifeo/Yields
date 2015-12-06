package yields.server.router

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.Tcp
import akka.stream.actor.ActorSubscriberMessage
import akka.testkit.TestActorRef
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

  lazy val hub = {
    val hubProps = ClientHub.props(self, InetSocketAddress.createUnresolved("", 0), self)
    TestActorRef[ClientHub](hubProps)
  }

  "ClientHub" should "refuse answering until user is connected" in {

    val searchResponse = serialize(Response(UserSearchRes(0), Metadata.now(0)))
    val connectResponse = serialize(Response(UserConnectRes(0, returning = true), Metadata.now(0)))
    val errorMessage = hub.underlyingActor.errorMessage

    hub ! OnNext(searchResponse)
    expectMsg(Write(errorMessage, WriteAck(errorMessage)))
    hub ! WriteAck(errorMessage)

    hub ! OnNext(searchResponse)
    expectMsg(Write(errorMessage, WriteAck(errorMessage)))
    hub ! WriteAck(errorMessage)

    hub ! OnNext(connectResponse)
    expectMsgAllOf(
      Write(connectResponse, WriteAck(connectResponse)),
      InitConnection(0)
    )
    hub ! WriteAck(connectResponse)

    hub ! OnNext(searchResponse)
    expectMsg(Write(searchResponse, WriteAck(searchResponse)))

  }

}
