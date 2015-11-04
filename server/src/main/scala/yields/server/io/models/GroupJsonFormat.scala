package yields.server.io.models

import java.time.OffsetDateTime

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.io._
import yields.server.dbi.models.Group
import yields.server.utils.Converters

/** Json writer for [[Group]]. */
object GroupJsonFormat extends RootJsonFormat[Group] {

  private val gidFld = "gid"
  private val nameFld = "name"
  private val lastActivityFld = "lastActivity"

  override def read(json: JsValue): Group = json.asJsObject.getFields(gidFld, nameFld, lastActivityFld) match {
    case Seq(JsString(gid), JsString(name), JsString(lastActivity)) =>
      val g = new Group
      g.id = gid
      g.group_name = name
      g.last_activity = Converters.offsetDateTime2Date(OffsetDateTime.parse(lastActivity))
      g
  }

  override def write(obj: Group): JsValue = JsObject(
    gidFld -> obj.id.toJson,
    nameFld -> obj.group_name.toJson,
    lastActivityFld -> Converters.date2OffsetDateTime(obj.last_activity).toJson
  )

}