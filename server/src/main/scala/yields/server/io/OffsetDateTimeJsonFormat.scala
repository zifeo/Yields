package yields.server.io

import java.time.OffsetDateTime

import spray.json._

/** Json format for [[OffsetDateTime]]. */
object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {

  override def write(obj: OffsetDateTime) = JsString(obj.toString)

  override def read(json: JsValue): OffsetDateTime = json match {
    case JsString(s) => OffsetDateTime.parse(s)
    case _ => deserializationError("invalid date format")
  }

}
