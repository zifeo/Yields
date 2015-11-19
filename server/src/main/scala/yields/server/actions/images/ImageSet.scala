package yields.server.actions.images

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Result, Action}
import yields.server.dbi.models.{Image, NID}
import yields.server.mpi.Metadata

/**
  * Upload an image on server
  */
case class ImageSet(content: String) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (content.nonEmpty) {
      val img = Image.createImage(content)
      ImageSetRes(img.nid)
    } else {
      val errorMessage = this.getClass.getSimpleName
      throw new ActionArgumentException(s"image content empty in : $errorMessage")
    }
  }
}


case class ImageSetRes(nid: NID) extends Result