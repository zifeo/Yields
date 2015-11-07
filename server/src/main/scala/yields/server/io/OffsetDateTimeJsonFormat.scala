package yields.server.io

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import spray.json._

object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {

  private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  override def write(obj: OffsetDateTime) = JsString(obj.format(formatter))

  override def read(json: JsValue): OffsetDateTime = json match {
    case JsString(s) => OffsetDateTime.parse(s)
    case _ => deserializationError("invalid date format")
  }

}
