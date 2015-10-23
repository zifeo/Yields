package yields.server.io.models

import spray.json._
import spray.json.DefaultJsonProtocol._
import yields.server.models.User

/** Json writer for [[yields.server.models.User]]. */
object UserJsonFormat extends RootJsonFormat[User] {

  @deprecated("spray-json#116", "always")
  override def read(ignored: JsValue): User = new User

  override def write(obj: User): JsValue = JsObject(
    "uid" -> obj.id.toJson,
    "name" -> obj.name.toJson
  )

}