package yields.server.io.models

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.dbi.models.Group
import yields.server.io._

/** Json format for [[Group]]. */
object GroupJsonFormat extends RootJsonFormat[Group] {

  private val nidFld = "nid"
  private val nameFld = "name"
  private val refreshedAtFld = "refreshed_at"

  override def write(obj: Group): JsValue = {
    JsObject(
      nidFld -> obj.nid.toJson,
      nameFld -> obj.name.toJson,
      refreshedAtFld -> obj.refreshed_at.toJson
    )
  }

  override def read(json: JsValue): Group =
    json.asJsObject.getFields(nidFld) match {
      case Seq(JsString(nid)) => Group(nid.toLong)
      case _ => deserializationError(s"bad group format: $json")
    }


}