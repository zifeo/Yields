package yields.server.mpi.io

import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import yields.server.mpi.UserUpdate

object UserUpdateJsonFormat extends RootJsonFormat[UserUpdate] {

  override def write(obj: UserUpdate): JsValue =
    JsObject(
      "uid" -> obj.uid.toJson,
      "email" -> obj.email.toJson,
      "name" -> obj.name.toJson,
      "image" -> obj.image.toJson
    )

  // TODO : handle image as array of bytes
  override def read(json: JsValue): UserUpdate =
    json.asJsObject.getFields("uid", "email", "name", "image") match {
      case Seq(JsNumber(uid), JsString(email), JsString(name), JsNull) =>
        UserUpdate(uid.toLong, Some(email), Some(name), None)
      case Seq(JsNumber(uid), JsString(email), JsNull, JsNull) =>
        UserUpdate(uid.toLong, Some(email), None, None)
      case Seq(JsNumber(uid), JsString(name), JsNull, JsNull) =>
        UserUpdate(uid.toLong, None, Some(name), None)
      case _ => deserializationError("UserUpdateJsonFormat expected")
    }

}
