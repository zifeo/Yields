package yields.server

import akka.stream.scaladsl.Tcp
import akka.util.ByteString
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.slf4j.LoggerFactory
import spray.json._
import yields.server.io._
import yields.server.mpi.MessagesGenerators
import yields.server.utils.Config

class YieldsTests extends FlatSpec with Matchers with BeforeAndAfterAll with MessagesGenerators {

  val logger = LoggerFactory.getLogger(getClass)
  val cases = 1

  val connection = Tcp().outgoingConnection(Config.getString("addr"), Config.getInt("port"))

  private val server = Yields

  override def beforeAll(): Unit = {
    logger.info("Starting server on background")
    server.start()
  }

  override def afterAll(): Unit = {
    server.close()
    logger.info("Stopping server on background")
  }

  "A client" should "be able to connect to the server" in {

    val (source, generated) = generateSource(requestArb.arbitrary, cases)

    await {
      source
        .map { case request =>
          ByteString(request.toJson.toString())
        }
        .via(connection)
        .map(_.utf8String)
        .runForeach(println)
    }

  }

}
