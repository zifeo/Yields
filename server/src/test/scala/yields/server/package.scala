package yields

import org.scalacheck.{Arbitrary, Gen}
import spray.json._

package object server {

  import Arbitrary.arbitrary

  /** Avoids string to contain end-of-input char. */
  private def avoidEOI(str: String): String = str.replace('\uFFFF', ' ')

  /** Serialize and deserialize a given element. */
  def toAndFromJson[T: JsonWriter: JsonReader](elem: T): T =
    elem.toJson.toString().parseJson.convertTo[T]

  lazy val cleanStringGen: Gen[String] =
    for {
      str <- arbitrary[String]
    } yield avoidEOI(str)

  lazy val cleanOptionStringGen: Gen[Option[String]] =
    for {
      opt <- arbitrary[Option[String]]
    } yield opt.map(avoidEOI)

}
