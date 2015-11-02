package yields.server.io.mpi

import spray.json._
import yields.server.actions.Action
import yields.server.io._
import yields.server.mpi.{Metadata, Request}

/** Json format for [[Request]]. */
object RequestJsonFormat extends RootJsonFormat[Request] {

  private val metadataFld = "metadata"

  override def write(obj: Request): JsValue = {
    val actionJson = obj.action.toJson.asJsObject
    val metadataJson = obj.metadata.toJson
    JsObject(actionJson.fields + (metadataFld -> metadataJson))
  }

  override def read(json: JsValue): Request = {
    val action = json.convertTo[Action]
    val metadata = json.asJsObject.getFields(metadataFld) match {
      case Seq(md) => md.convertTo[Metadata]
      case _ => deserializationError(s"unknown metadata")
    }
    Request(action, metadata)
  }

}