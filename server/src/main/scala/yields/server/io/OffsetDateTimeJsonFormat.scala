package yields.server.io

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import spray.json.{JsString, JsValue, RootJsonFormat}
import yields.server.actions.exceptions.SerializationException

object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {

  private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  override def write(obj: OffsetDateTime) = JsString(obj.format(formatter))

  override def read(json: JsValue): OffsetDateTime = json match {
    case JsString(s) => OffsetDateTime.parse(s)
    case _ => throw new SerializationException("invalid date format")
  }

}
