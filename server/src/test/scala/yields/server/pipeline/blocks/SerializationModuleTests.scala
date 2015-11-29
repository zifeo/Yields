package yields.server.pipeline.blocks

import akka.stream.scaladsl.Flow
import akka.stream.testkit.scaladsl.TestSink
import akka.util.ByteString
import org.scalatest.FlatSpec
import spray.json._
import yields.server._
import yields.server.io._
import yields.server.mpi.{Response, MessagesGenerators, Request}

class SerializationModuleTests extends FlatSpec with MessagesGenerators {

  val logger = new FakeLogger
  val module = SerializationModule()(logger)
  val cases = 25

  "A serialization module" should "not change any value when de-serializing and serializing" in {

    val zipGen = requestArb.arbitrary.map(request => request -> responseArb.arbitrary.sample.get)
    val (source, generated) = generateSource(zipGen, cases)

    val correspondence = generated.toMap.map { case (req, res) =>
      req.toJson.toString -> res
    }
    val mapping = Flow[Request].map(req => correspondence(req.toJson.toString))

    source
      .map { case (request, _) =>
        ByteString(request.toJson.toString())
      }
      .via(module.join(mapping))
      .map(_.utf8String)
      .runWith(TestSink.probe[String])
      .request(cases)
      .expectNextN(generated.map { case (_, response) =>
        val json = response.toJson
        s"$json\n"
      })
      .expectComplete()
  }

}
