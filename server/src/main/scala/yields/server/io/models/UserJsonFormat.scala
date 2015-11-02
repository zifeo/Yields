package yields.server.io.models

import spray.json._
import spray.json.DefaultJsonProtocol._
import yields.server.dbi.models.User

/** Json writer for [[yields.server.dbi.models.User]]. */
object UserJsonFormat extends RootJsonFormat[User] {

  private val uidFld = "uid"
  private val nameFld = "name"

  override def read(json: JsValue): User = json.asJsObject.getFields(uidFld, nameFld) match {
    case Seq(JsString(uid), JsString(name)) =>
      val u = new User
      u.id = uid
      u.name = name
      u
  }

  override def write(obj: User): JsValue = JsObject(
    uidFld-> obj.id.toJson,
    nameFld -> obj.name.toJson
  )

}