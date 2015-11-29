package yields.server.io.mpi

import spray.json._
import yields.server.io._
import yields.server.actions.Broadcast
import yields.server.mpi.{Metadata, Notification}

/** Json format for [[Notification]]. */
object NotificationJsonFormat extends RootJsonFormat[Notification] {

  private val metadataFld = "metadata"

  override def write(obj: Notification): JsValue = {
    val resultJson = obj.bcast.toJson.asJsObject
    val metadataJson = obj.metadata.toJson
    JsObject(resultJson.fields + (metadataFld -> metadataJson))
  }

  override def read(json: JsValue): Notification = {
    val bcast = json.convertTo[Broadcast]
    val metadata = json.asJsObject.getFields(metadataFld) match {
      case Seq(md) => md.convertTo[Metadata]
      case _ => deserializationError(s"unknown metadata")
    }
    Notification(bcast, metadata)
  }

}