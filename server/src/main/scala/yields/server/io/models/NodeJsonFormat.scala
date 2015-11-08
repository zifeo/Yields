package yields.server.io.models

import spray.json._
import yields.server.dbi.models.{Group, Node}
import yields.server.io._

/** Json writer for [[Node]]. */
object NodeJsonFormat extends RootJsonWriter[Node] {

  override def write(obj: Node): JsValue = obj match {
    case x: Group => packWithKind(x)
  }

}