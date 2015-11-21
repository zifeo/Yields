package yields

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.scalacheck.Gen
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

package object server {

  implicit lazy val system = ActorSystem("Yields-server-test")
  implicit lazy val materializer = ActorMaterializer()

  /**
    * Generates elements given the generator and creates a source from it.
    * FIXME 'scala.collection.immutable.Seq' is to avoid type confusion.
    * @param gen element generator
    * @param num number of generated elements, by default 20 (be careful on test timeout if too many)
    * @tparam T type of element
    * @return pair of a source and a sequence of the same elements
    */
  def generateSource[T](gen: Gen[T], num: Int): (Source[T, Unit], scala.collection.immutable.Seq[T]) = {
    val generated = (0 until num).flatMap(_ => gen.sample)
    (Source(generated), generated)
  }

  /** Blocks 3 second until result is available. */
  def await[T](run: Future[T]): T = {
    Await.result(run, 3 second)
  }

  /** Serialize and deserialize a given element. */
  def toAndFromJson[T : JsonWriter : JsonReader](elem: T): T =
    elem.toJson.toString().parseJson.convertTo[T]

}
