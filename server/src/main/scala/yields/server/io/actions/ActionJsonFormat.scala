package yields.server.io.actions

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.exceptions.SerializationException
import yields.server.actions.groups.{GroupCreate, GroupHistory, GroupMessage, GroupUpdate}
import yields.server.actions.users.{UserConnect, UserGroupList, UserUpdate}
import yields.server.io._

/** Json format for [[Action]]. */
object ActionJsonFormat extends RootJsonFormat[Action] {

  private val kindFld = "kind"
  private val messageFld = "message"

  /**
   * Format the message with its message type.
   * @param obj message to pack
   * @tparam T message type
   * @return packed message json format
   */
  def packWithKind[T: JsonWriter](obj: T): JsValue = JsObject(
    kindFld -> obj.getClass.getSimpleName.toJson,
    messageFld -> obj.toJson
  )

  override def write(obj: Action): JsValue = {
    val kind = obj.getClass.getSimpleName
    obj match {
      case x: GroupCreate => packWithKind(x)
      case x: GroupUpdate => packWithKind(x)
      case x: GroupMessage => packWithKind(x)
      case x: GroupHistory => packWithKind(x)

      case x: UserConnect => packWithKind(x)
      case x: UserUpdate => packWithKind(x)
      case x: UserGroupList => packWithKind(x)

      case _ => throw SerializationException(s"unregistered action kind: $kind")
    }
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

          case _ => throw SerializationException(s"unregistered action kind: $kind")
        }
      case _ => throw SerializationException(s"bad action format: $json")
    }

}