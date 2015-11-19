package yields.server.io.actions

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.groups.{GroupCreate, GroupUpdate}
import yields.server.actions.nodes.{NodeHistory, NodeMessage}
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
    case x: NodeMessage => packWithKind(x)
    case x: NodeHistory => packWithKind(x)

    case x: UserConnect => packWithKind(x)
    case x: UserUpdate => packWithKind(x)
    case x: UserGroupList => packWithKind(x)
    case x: UserInfo => packWithKind(x)

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
          case "NodeMessage" => message.convertTo[NodeMessage]
          case "NodeHistory" => message.convertTo[NodeHistory]

          case "UserConnect" => message.convertTo[UserConnect]
          case "UserUpdate" => message.convertTo[UserUpdate]
          case "UserGroupList" => message.convertTo[UserGroupList]
          case "UserInfo" => message.convertTo[UserInfo]

          case _ => deserializationError(s"unregistered action kind: $kind")
        }
      case _ => deserializationError(s"bad action format: $json")
    }

}