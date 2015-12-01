package yields.server.io.actions

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeMessage, NodeSearch, NodeHistory}
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
    case x: GroupInfo => packWithKind(x)
    case x: GroupMessage => packWithKind(x)

    case x: NodeSearch => packWithKind(x)
    case x: NodeHistory => packWithKind(x)

    case x: UserConnect => packWithKind(x)
    case x: UserUpdate => packWithKind(x)
    case x: UserNodeList => packWithKind(x)
    case x: UserInfo => packWithKind(x)
    case x: UserSearch => packWithKind(x)

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
          case "GroupInfo" => message.convertTo[GroupInfo]
          case "GroupMessage" => message.convertTo[GroupMessage]

          case "NodeSearch" => message.convertTo[NodeSearch]
          case "NodeHistory" => message.convertTo[NodeHistory]

          case "UserConnect" => message.convertTo[UserConnect]
          case "UserUpdate" => message.convertTo[UserUpdate]
          case "UserNodeList" => message.convertTo[UserNodeList]
          case "UserInfo" => message.convertTo[UserInfo]
          case "UserSearch" => message.convertTo[UserSearch]

          case _ => deserializationError(s"unregistered action kind: $kind")
        }
      case _ => deserializationError(s"bad action format: $json")
    }

}