package yields.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.scalacheck.Gen
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

package object tests {

  implicit lazy val system = ActorSystem("Yields-server-test")
  implicit lazy val materializer = ActorMaterializer()

  /**
    * Generates elements given the generator and creates a source from it.
    * @param gen element generator
    * @param num number of generated elements, by default 20 (be careful on test timeout if too many)
    * @tparam T type of element
    * @return pair of a source and a sequence of the same elements
    */
  def generateSource[T](gen: Gen[T], num: Int): (Source[T, Unit], List[T]) = {
    val generated = (0 until num).flatMap(_ => gen.sample).toList
    (Source(generated), generated)
  }

  /** Blocks 4 second until result is available. */
  def await[T](run: Future[T]): T = {
    Await.result(run, 4 seconds)
  }

  /** Serialize and deserialize a given element and return true on equality. */
  def toAndFromJson[T : JsonWriter : JsonReader](elem: T): Boolean = {
    val encoded = elem.toJson.toString()
    val decoded = encoded.parseJson.convertTo[T].toJson.toString()
    encoded == decoded
  }

}
