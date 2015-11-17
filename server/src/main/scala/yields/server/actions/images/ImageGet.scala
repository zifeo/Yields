package yields.server.actions.images

import yields.server.actions.{Result, Action}
import yields.server.dbi.models.NID
import yields.server.mpi.Metadata

/**
  * Get an image from an node id
  */
class ImageGet(nid: NID) extends Action {
  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = ???
}

case class ImageGetRes(base64Image: String) extends Result
