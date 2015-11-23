package yields.server.pipeline.blocks

import akka.stream.scaladsl.Flow
import akka.stream.testkit.scaladsl.TestSink
import akka.util.ByteString
import org.scalatest.FlatSpec
import spray.json._
import yields.server._
import yields.server.io._
import yields.server.mpi.{MessagesGenerators, Request}
import yields.server.pipeline.FakeLogger

class SerializationModuleTests extends FlatSpec with MessagesGenerators {

  val logger = new FakeLogger
  val module = SerializationModule()(logger)
  val cases = 25

  "A serialization module" should "not change any value when de-serializing and serializing" in {

    val zipGen = requestArb.arbitrary.map(request => request -> responseArb.arbitrary.sample.get)
    val (source, generated) = generateSource(zipGen, cases)
    val correspondence = generated.toMap

    source
      .map { case (request, _) =>
        ByteString(request.toJson.toString())
      }
      .via(module.join(Flow[Request].map(correspondence)))
      .map(_.utf8String)
      .runWith(TestSink.probe[String])
      .request(cases)
      .expectNextN(generated.map { case (_, response) =>
        val json = response.toJson.toString()
        s"$json\n"
      })
      .expectComplete()
  }

}
