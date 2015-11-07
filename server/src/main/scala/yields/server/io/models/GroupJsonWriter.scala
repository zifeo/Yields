package yields.server.io.models

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.dbi.models.Group
import yields.server.io._

/** Json writer for [[Group]]. */
object GroupJsonWriter extends RootJsonWriter[Group] {

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

}