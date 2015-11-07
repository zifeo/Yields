package yields.server.io.mpi

import spray.json._
import yields.server.actions.Result
import yields.server.io._
import yields.server.mpi.{Metadata, Response}

/** Json format for [[Response]]. */
object ResponseJsonFormat extends RootJsonFormat[Response] {

  private val metadataFld = "metadata"

  override def write(obj: Response): JsValue = {
    val resultJson = obj.result.toJson.asJsObject
    val metadataJson = obj.metadata.toJson
    JsObject(resultJson.fields + (metadataFld -> metadataJson))
  }

  override def read(json: JsValue): Response = {
    val result = json.convertTo[Result]
    val metadata = json.asJsObject.getFields(metadataFld) match {
      case Seq(md) => md.convertTo[Metadata]
      case _ => deserializationError(s"unknown metadata")
    }
    Response(result, metadata)
  }

}