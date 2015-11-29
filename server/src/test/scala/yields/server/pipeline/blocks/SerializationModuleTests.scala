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
    val correspondence = generated.toMap

    // Array of byte make some correspondence fail as they do not have a good equality, in this case take the fallback
    val mapping = Flow[Request].map { req =>
      lazy val fallback = correspondence.find(_._1.metadata == req.metadata).getOrElse(throw new Exception("no luck"))
      correspondence.getOrElse(req, fallback._2)
    }

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
