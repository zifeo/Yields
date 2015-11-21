package yields.server.pipeline.blocks

import akka.event.LoggingAdapter
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.scalacheck.Gen
import org.scalatest.{FlatSpec, Matchers}
import yields.server.DefaultsGenerators
import yields.server.pipeline._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class LoggerModuleTests extends FlatSpec with Matchers with DefaultsGenerators {

  /** Fakes a logger and allows access to all logs. */
  final class FakeLogger extends LoggingAdapter {

    val debug = mutable.ListBuffer.empty[String]
    val info = mutable.ListBuffer.empty[String]
    val warning = mutable.ListBuffer.empty[String]
    val error = mutable.ListBuffer.empty[String]

    override def isDebugEnabled: Boolean = true
    override def isInfoEnabled: Boolean = true
    override def isWarningEnabled: Boolean = true
    override def isErrorEnabled: Boolean = true

    override protected def notifyDebug(message: String): Unit = debug.synchronized(debug += message)
    override protected def notifyInfo(message: String): Unit = info.synchronized(info += message)
    override protected def notifyWarning(message: String): Unit = warning.synchronized(warning += message)
    override protected def notifyError(message: String): Unit = error.synchronized(error += message)
    override protected def notifyError(cause: Throwable, message: String): Unit = error.synchronized(error += message)

    /** Trashes all logs. */
    def clear(): Unit = {
      debug.clear()
      info.clear()
      warning.clear()
      error.clear()
    }
  }

  val logger = new FakeLogger
  val module = LoggerModule[String, String]()(logger)

  /**
    * Generates elements given the generator and creates a source from it.
    * FIXME 'scala.collection.immutable.Seq' is to avoid type confusion.
    * @param gen element generator
    * @param num number of generated elements, by default 20 (be careful on test timeout if too many)
    * @tparam T type of element
    * @return pair of a source and a sequence of the same elements
    */
  def sourceGen[T](gen: Gen[T], num: Int = 20): (Source[T, Unit], scala.collection.immutable.Seq[T]) = {
    val generated = (0 until num).flatMap(_ => gen.sample)
    (Source(generated), generated)
  }

  /** Blocks 1 second until result is available. */
  def await[T](run: Future[T]): T = {
    Await.result(run, 1 second)
  }

  "A logger module" should "not change any value" in {

    val (source, generated) = sourceGen(stringArb.arbitrary)
    source
      .via(module.join(Flow[String]))
      .runWith(TestSink.probe[String])
      .request(100)
      .expectNextN(generated)
      .expectComplete()

  }

  it should "log incoming value" in {

    logger.clear()
    val (source, generated) = sourceGen(stringArb.arbitrary)

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
