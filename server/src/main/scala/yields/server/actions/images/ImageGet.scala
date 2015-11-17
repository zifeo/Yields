package yields.server.actions.images

import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Image, NID}
import yields.server.mpi.Metadata

/**
  * Get an image from an node id
  */
case class ImageGet(nid: NID) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    val img = Image(nid)
    ImageGetRes(img.content)
  }
}

case class ImageGetRes(base64Image: String) extends Result
