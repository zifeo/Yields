package yields.server

import akka.stream.scaladsl.{Sink, Source, Tcp}
import org.scalatest.{FlatSpec, BeforeAndAfterAll, Matchers}
import yields.server.actions.groups._
import yields.server.actions.users.{UserConnect, UserConnectRes, UserUpdate, UserUpdateRes}
import yields.server.actions.{Action, Result}
import yields.server.dbi._
import yields.server.io._
import yields.server.mpi.{MessagesGenerators, Metadata, Response}
import yields.server.pipeline.blocks.SerializationModule
import yields.server.tests.{FakeClient, _}
import yields.server.utils.Config

import scala.language.implicitConversions

class YieldsTests extends DBFlatSpec with Matchers with BeforeAndAfterAll with MessagesGenerators {

  val connection = Tcp().outgoingConnection(Config.getString("addr"), Config.getInt("port"))

  private val server = Yields

  override def beforeAll(): Unit = {
    server.start()
    Thread.sleep(4000) // ensure server is active and bounded before networking with it
  }

  override def afterAll(): Unit = {
    server.stop()
  }

  "A client with a socket" should "be able to connect to the server" in {

    val client = new FakeClient(1)
    client.send(UserConnect("client@yields.im"))
    await(client.receive()).result should be (UserConnectRes(1, returning = false))
    client.close()

  }

  it should "receive pushes from the server" in {

    val clientA = new FakeClient(1)
    val clientB = new FakeClient(2)

    clientA.send(UserConnect("clientA@yields.im"))
    await(clientA.receive()).result should be (UserConnectRes(clientA.uid, returning = false))

    clientB.send(UserConnect("clientB@yields.im"))
    await(clientB.receive()).result should be (UserConnectRes(clientB.uid, returning = false))

    val refUpdate = clientA.send(UserUpdate(None, None, None, List(clientB.uid), List.empty))
    await(clientA.receive()).result should be (UserUpdateRes())
    await(clientB.listen()).metadata.ref should be (refUpdate)

    val refCreate = clientA.send(GroupCreate("clients", List(clientA.uid, clientB.uid), List.empty))
    await(clientB.listen()).metadata.ref should be (refCreate)
    await(clientA.receive()).result should be (GroupCreateRes(3))

    val refMessage = clientB.send(GroupMessage(3, Some("hello"), None, None))
    await(clientA.listen()).metadata.ref should be (refMessage)
    await(clientB.receive()).metadata.ref should be (refMessage)

    clientA.close()
    clientB.close()

  }

  "The system" should "not generate error caused too many requests" in {

    val client = new FakeClient(1)
    val tries = 50

    client.send(UserConnect("client@yields.im"))
    await(client.receive()).result should be (UserConnectRes(client.uid, returning = false))

    client.send(GroupCreate("clients", List.empty, List.empty))
    await(client.receive()).result should be (GroupCreateRes(2))

    Thread.sleep(3000)

    for (_ <- 0 to tries) {
      client.send(GroupMessage(2, Some("hello"), None, None))
    }

    for (_ <- 0 to tries) {
      await(client.receive())
    }

    val sent = client.requests.map(_.metadata.ref)
    val received = client.responses.map(_.metadata.ref)
    sent should contain theSameElementsAs received

    client.close()

  }

}
