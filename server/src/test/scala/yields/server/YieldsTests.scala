package yields.server

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.time.OffsetDateTime

import akka.stream.scaladsl.{Sink, Source, Tcp}
import akka.util.ByteString
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.slf4j.LoggerFactory
import spray.json._
import yields.server.actions.groups._
import yields.server.actions.users.{UserConnect, UserConnectRes}
import yields.server.actions.{Action, Result}
import yields.server.dbi._
import yields.server.dbi.models.UID
import yields.server.io._
import yields.server.mpi.{MessagesGenerators, Metadata, Request, Response}
import yields.server.utils.{Config, Temporal}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

class YieldsTests extends FlatSpec with Matchers with BeforeAndAfterAll with MessagesGenerators {

  val logger = LoggerFactory.getLogger(getClass)
  val connection = Tcp().outgoingConnection(Config.getString("addr"), Config.getInt("port"))

  private val server = Yields

  override def beforeAll(): Unit = {
    logger.info("Starting server on background")
    redis(_.flushdb)
    server.start()
    Thread.sleep(1000) // ensure server is started
  }

  override def afterAll(): Unit = {
    server.close()
    redis(_.flushdb)
    logger.info("Stopping server on background")
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

  /** Fakes a client connection through a socket. */
  class FakeClient(uid: UID) {

    private val socket = new Socket(Config.getString("addr"), Config.getInt("port"))
    private val receiver = new BufferedReader(new InputStreamReader(socket.getInputStream))
    private val sender = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))

    /** Send a request to the server. */
    def send(request: Request): Unit = {
      sender.write(request.toJson.toString())
      sender.newLine()
      sender.flush()
    }

    /** Send an action to the server. */
    def send(action: Action): OffsetDateTime = {
      val metadata = Metadata.now(uid)
      send(Request(action, metadata))
      metadata.ref
    }

    /** Gets next response from the server. */
    def receive(): Future[Response] = Future {
      val message = receiver.readLine()
      message.parseJson.convertTo[Response]
    }

    def close(): Unit = {
      socket.close()
    }

  }

  "A client with a socket" should "be able to connect to the server" in {
    val client = new FakeClient(1)
    client.send(UserConnect("client@yields.im"))
    await(client.receive()).result should be (UserConnectRes(1))
  }

  it should "receive pushes from the server" in {
    val clientA = new FakeClient(1)
    val clientB = new FakeClient(2)

    clientA.send(UserConnect("clientA@yields.im"))
    await(clientA.receive()).result should be (UserConnectRes(1))

    clientB.send(UserConnect("clientB@yields.im"))
    await(clientB.receive()).result should be (UserConnectRes(2))

    clientA.send(GroupCreate("clients", Seq.empty, Seq(1, 2)))
    await(clientA.receive()).result should be (GroupCreateRes(1))

    val ref = clientB.send(GroupMessage(1, "hello"))
    await(clientB.receive()).metadata.ref should be (ref)
    await(clientA.receive()).metadata.ref should be (ref)
  }

}
