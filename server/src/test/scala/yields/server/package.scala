package yields

import spray.json._

package object server {

  /** Serialize and deserialize a given element. */
  def toAndFromJson[T: JsonWriter : JsonReader](elem: T): T =
    elem.toJson.toString().parseJson.convertTo[T]

}
