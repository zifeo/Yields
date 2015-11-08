package yields.server.io.models

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.dbi.models.{Group, Node}
import yields.server.io._

/** Json writer for [[Node]]. */
object NodeJsonFormat extends RootJsonFormat[Node] {

  private val kindFld = "kind"
  private val nodeFld = "node"

  /**
    * Format given object with its type.
    * @param obj object to pack
    * @tparam T object type
    * @return packed json object
    */
  def packWithKind[T: JsonWriter](obj: T): JsValue = JsObject(
    kindFld -> obj.getClass.getSimpleName.toJson,
    nodeFld -> obj.toJson
  )

  override def write(obj: Node): JsValue = obj match {
    case x: Group => packWithKind(x)
  }

  override def read(json: JsValue): Node =
    json.asJsObject.getFields(kindFld, nodeFld) match {
      case Seq(JsString(kind), message) =>
        kind match {
          case "Group" => message.convertTo[Group]
          case _ => deserializationError(s"unregistered node kind: $kind")
        }
      case _ => deserializationError(s"bad node format: $json")
    }

}