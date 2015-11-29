package yields.server

import akka.stream.scaladsl.{Sink, Source, Tcp}
import akka.util.ByteString
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.slf4j.LoggerFactory
import spray.json._
import yields.server.actions.groups._
import yields.server.actions.nodes.NodeMessage
import yields.server.actions.users.{UserUpdateRes, UserUpdate, UserConnect, UserConnectRes}
import yields.server.actions.{Action, Result}
import yields.server.dbi._
import yields.server.io._
import yields.server.mpi.{MessagesGenerators, Metadata, Request, Response}
import yields.server.utils.Config

import scala.language.implicitConversions

class YieldsTests extends DBFlatSpec with Matchers with BeforeAndAfterAll with MessagesGenerators {

  val logger = LoggerFactory.getLogger(getClass)
  val connection = Tcp().outgoingConnection(Config.getString("addr"), Config.getInt("port"))

  private val server = Yields

  override def beforeAll(): Unit = {
    logger.info("Starting local server")
    server.start()
    Thread.sleep(4000) // ensure server is active and bounded before networking with it
  }

  override def afterAll(): Unit = {
    server.close()
    logger.info("Stopping local server")
  }

  /**
    * Creates and runs some actions expecting some result.
    * TODO : refactor this method with socket.
    *
    * {{{
    * "A client without a socket" should "be able to send and retrieve message" in scenario (
    *   GroupCreate("test group", Seq.empty, Seq(1)) -> GroupCreateRes(1),
    *   GroupMessage(1, "test message") -> None,
    *   GroupHistory(1, Temporal.current, 1) -> None
    * }
    * }}}
    * @param acting some actions resulting in their results
    */
  def scenario(acting: (Action, Option[Result])*): Unit = {
    require(acting.nonEmpty, "scenario must contains some acting")

    val metadata = Metadata.now(1)
    val (actions, expected) = acting.toList.unzip
    val requests = actions.map { action =>
      val json = Request(action, metadata).toJson.toString
      ByteString(s"$json\n")
    }

    val results = await {
      Source(requests)
        .via(connection)
        .map(_.utf8String.parseJson.convertTo[Response].result)
        .grouped(expected.size)
        .runWith(Sink.head)
    }.toList

    results should have size expected.size
    expected.flatten foreach (results should contain (_))
  }

  implicit def results2OptionResults(result: Result): Option[Result] = Some(result)

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
    val tries = 100

    client.send(UserConnect("client@yields.im"))
    await(client.receive()).result should be (UserConnectRes(client.uid, returning = false))

    client.send(GroupCreate("clients", List.empty, List.empty))
    await(client.receive()).result should be (GroupCreateRes(2))

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
