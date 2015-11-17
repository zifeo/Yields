package yields.server.io.actions

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.groups.{GroupCreate, GroupHistory, GroupMessage, GroupUpdate}
import yields.server.actions.images.{ImageGet, ImageSet}
import yields.server.actions.users._
import yields.server.io._

/** Json format for [[Action]]. */
object ActionJsonFormat extends RootJsonFormat[Action] {

  private val kindFld = "kind"
  private val messageFld = "message"

  /**
    * Format given object with its type.
    * @param obj object to pack
    * @tparam T object type
    * @return packed json object
    */
  def packWithKind[T: JsonWriter](obj: T): JsValue = JsObject(
    kindFld -> obj.getClass.getSimpleName.toJson,
    messageFld -> obj.toJson
  )

  override def write(obj: Action): JsValue = obj match {
    case x: GroupCreate => packWithKind(x)
    case x: GroupUpdate => packWithKind(x)
    case x: GroupMessage => packWithKind(x)
    case x: GroupHistory => packWithKind(x)

    case x: UserConnect => packWithKind(x)
    case x: UserUpdate => packWithKind(x)
    case x: UserGroupList => packWithKind(x)
    case x: UserCreate => packWithKind(x)
    case x: UserInfo => packWithKind(x)

    case x: ImageSet => packWithKind(x)
    case x: ImageGet => packWithKind(x)

    case _ =>
      val kind = obj.getClass.getSimpleName
      serializationError(s"unregistered action kind: $kind")
  }

  override def read(json: JsValue): Action =
    json.asJsObject.getFields(kindFld, messageFld) match {
      case Seq(JsString(kind), message) =>
        kind match {
          case "GroupCreate" => message.convertTo[GroupCreate]
          case "GroupUpdate" => message.convertTo[GroupUpdate]
          case "GroupMessage" => message.convertTo[GroupMessage]
          case "GroupHistory" => message.convertTo[GroupHistory]

          case "UserConnect" => message.convertTo[UserConnect]
          case "UserUpdate" => message.convertTo[UserUpdate]
          case "UserGroupList" => message.convertTo[UserGroupList]
          case "UserCreate" => message.convertTo[UserCreate]
          case "UserInfo" => message.convertTo[UserInfo]

          case "ImageSet" => message.convertTo[ImageSet]
          case "ImageGet" => message.convertTo[ImageGet]

          case _ => deserializationError(s"unregistered action kind: $kind")
        }
      case _ => deserializationError(s"bad action format: $json")
    }

}