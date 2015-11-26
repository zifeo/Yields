package yields.server.actions.groups

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models.{Blob, Group, NID}
import yields.server.mpi.Metadata

/**
  * Update a group given given specific fields.
  * @param nid group id
  * @param name new name
  * @param pic new profile image
  *
  *            TODO: Update picture
  */
case class GroupUpdate(nid: NID, name: Option[String], pic: Option[Blob]) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (nid > 0) {
      val group = Group(nid)

      name match {
        case Some(newName) if newName.nonEmpty => group.name = newName
        case _ =>
      }

      GroupUpdateRes()
    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"Bad nid in : $errorMessage")
    }
  }

}

/** [[GroupUpdate]] result. */
case class GroupUpdateRes() extends Result
