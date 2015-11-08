package yields.server.io.models

import spray.json._
import spray.json.DefaultJsonProtocol._
import yields.server.dbi.models.User

/** Json writer for [[yields.server.dbi.models.User]]. */
object UserJsonFormat extends RootJsonFormat[User] {

  private val uidFld = "uid"
  private val nameFld = "name"

  override def write(obj: User): JsValue = JsObject(
    uidFld-> obj.uid.toJson,
    nameFld -> obj.name.toJson
  )

  override def read(json: JsValue): User =
    json.asJsObject.getFields(uidFld) match {
      case Seq(JsString(uid)) => User(uid.toLong)
      case _ => deserializationError(s"bad user format: $json")
    }

}