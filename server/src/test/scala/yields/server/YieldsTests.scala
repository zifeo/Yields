package yields.server

import akka.stream.scaladsl.{Source, Tcp}
import akka.stream.testkit.scaladsl.TestSink
import akka.util.ByteString
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.slf4j.LoggerFactory
import spray.json._
import yields.server.actions.groups._
import yields.server.actions.users.{UserConnect, UserConnectRes}
import yields.server.actions.{Action, Result}
import yields.server.dbi._
import yields.server.io._
import yields.server.mpi.{MessagesGenerators, Metadata, Request, Response}
import yields.server.utils.{Config, Temporal}

import scala.language.implicitConversions

class YieldsTests extends FlatSpec with Matchers with BeforeAndAfterAll with MessagesGenerators {

  val logger = LoggerFactory.getLogger(getClass)
  val connection = Tcp().outgoingConnection(Config.getString("addr"), Config.getInt("port"))

  private val server = Yields

  override def beforeAll(): Unit = {
    logger.info("Starting server on background")
    redis(_.flushdb)
    server.start()
  }

  override def afterAll(): Unit = {
    server.close()
    redis(_.flushdb)
    logger.info("Stopping server on background")
  }

  /** Creates and runs some actions expecting some result. */
  def scenario(acting: (Action, Option[Result])*): Unit = {
    require(acting.nonEmpty, "scenario must contains some acting")

    val metadata = Metadata(1, Temporal.current)
    val (actions, results) = acting.toList.unzip
    val requests = actions.map { action =>
      val json = Request(action, metadata).toJson
      ByteString(json.toString())
    }

    val probe =
      Source(requests)
      .via(connection)
      .map(_.utf8String.parseJson.convertTo[Response].result)
      .runWith(TestSink.probe[Result])
      .request(results.size)

    results.foreach {
      case Some(res) => probe.expectNext(res)
      case None => probe.expectNextN(1)
    }
    probe.expectComplete()
  }

  implicit def results2OptionResults(result: Result): Option[Result] = Some(result)

  "A client" should "be able to connect to the server" in scenario (
    UserConnect("tests@yields.im") -> Some(UserConnectRes(1))
  )

  it should "be able to send and retrieve message" in scenario (
    GroupCreate("test group", Seq.empty, Seq(1)) -> GroupCreateRes(1),
    GroupMessage(1, "test message") -> None,
    GroupHistory(1, Temporal.current, 1) -> None
  )

}
