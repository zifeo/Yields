package yields.server.actions.images

import yields.server.actions.{Result, Action}
import yields.server.dbi.models.NID
import yields.server.mpi.Metadata

/**
  * Upload an image on server
  */
class ImageSet(content: String) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = ???
}


case class ImageSetRes(nid: NID) extends Result