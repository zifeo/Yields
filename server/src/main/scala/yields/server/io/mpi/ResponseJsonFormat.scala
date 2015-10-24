package yields.server.io.mpi

import spray.json._
import yields.server.actions.Result
import yields.server.mpi.{Metadata, Response}

/** Json format for [[Response]]. */
object ResponseJsonFormat extends RootJsonFormat[Response] {

  override def write(obj: Response): JsValue = {
    val resultJson = obj.result.toJson.asJsObject
    val metadataJson = obj.metadata.toJson.asJsObject
    JsObject(resultJson.fields ++ metadataJson.fields)
  }

  override def read(json: JsValue): Response = {
    val result = json.convertTo[Result]
    val metadata = json.convertTo[Metadata]
    Response(result, metadata)
  }

}