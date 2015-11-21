package yields.server.pipeline.blocks

import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{FlatSpec, Matchers}
import yields.server.DefaultsGenerators
import yields.server.pipeline._

class LoggerModuleTests extends FlatSpec with Matchers with DefaultsGenerators {

  val logger = new FakeLogger
  val module = LoggerModule[String, String]()(logger)

  "A logger module" should "not change any value" in {

    val (source, generated) = generateSource(stringArb.arbitrary)
    source
      .via(module.join(Flow[String]))
      .runWith(TestSink.probe[String])
      .request(100)
      .expectNextN(generated)
      .expectComplete()
  }

  it should "log incoming and outgoing values" in {

    logger.clear()
    val (source, generated) = generateSource(stringArb.arbitrary)

    await {
      source
        .via(module.join(Flow[String]))
        .runWith(Sink.ignore)
    }

    generated.foreach { element =>
      val occurrences = logger.info.filter(_.contains(element))
      occurrences should contain inOrder (s"[IN] $element", s"[OUT] $element")
    }
  }

}
