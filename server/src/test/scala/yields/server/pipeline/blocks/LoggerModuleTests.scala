package yields.server.pipeline.blocks

import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.testkit.scaladsl.TestSink
import yields.server.tests.{FakeLogger, _}

class LoggerModuleTests extends YieldsSpec {

  val logger = new FakeLogger
  val module = LoggerModule[String, String]()(logger)
  val cases = 25

  "A logger module" should "not change any value" in {

    val (source, generated) = generateSource(stringArb.arbitrary, cases)
    source
      .via(module.join(Flow[String]))
      .runWith(TestSink.probe[String])
      .request(cases)
      .expectNextN(generated)
      .expectComplete()
  }

  it should "log incoming and outgoing values" in {

    logger.clear()
    val (source, generated) = generateSource(stringArb.arbitrary, cases)

    await {
      source
        .via(module.join(Flow[String]))
        .runWith(Sink.ignore)
    }

    generated.foreach { element =>
      val occurrences = logger.info.filter(_.contains(element))
      occurrences should contain inOrder (s"[INP] $element", s"[OUT] $element")
    }
  }

}
