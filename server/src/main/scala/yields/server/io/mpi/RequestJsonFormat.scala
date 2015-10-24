package yields.server.io.mpi

import spray.json._
import yields.server.actions.Action
import yields.server.mpi.{Metadata, Request}

/** Json format for [[Request]]. */
object RequestJsonFormat extends RootJsonFormat[Request] {

  override def write(obj: Request): JsValue = {
    val actionJson = obj.action.toJson.asJsObject
    val metadataJson = obj.metadata.toJson.asJsObject
    JsObject(actionJson.fields ++ metadataJson.fields)
  }

  override def read(json: JsValue): Request = {
    val action = json.convertTo[Action]
    val metadata = json.convertTo[Metadata]
    Request(action, metadata)
  }

}