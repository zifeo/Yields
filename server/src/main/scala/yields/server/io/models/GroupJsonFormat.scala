package yields.server.io.models

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.io._
import yields.server.models.Group
import yields.server.utils.Converters

/** Json writer for [[Group]]. */
object GroupJsonFormat extends RootJsonFormat[Group] {

  @deprecated("spray-json#116", "always")
  override def read(ignored: JsValue): Group = new Group

  override def write(obj: Group): JsValue = JsObject(
    "gid" -> obj.id.toJson,
    "name" -> obj.group_name.toJson,
    "lastActivity" -> Converters.date2OffsetDateTime(obj.last_activity).toJson
  )

}