package yields.server.actions.groups

import yields.server.actions.exceptions.ActionArgumentException
import yields.server.actions.{Action, Result}
import yields.server.dbi.models._
import yields.server.dbi.tags.Tag
import yields.server.mpi.Metadata

/**
  * Creation of a named group including some nodes
  * @param name group name
  * @param nodes grouped nodes
  * @param users users to put in the group
  * @param visibility private or public
  */
case class GroupCreate(name: String, nodes: Seq[NID], users: Seq[UID], tags: Seq[String], visibility: String) extends Action {

  /**
    * Run the action given the sender.
    * @param metadata action requester
    * @return action result
    */
  override def run(metadata: Metadata): Result = {
    if (!name.isEmpty) {
      if (visibility == "private" || visibility == "public") {

        val group = Group.createGroup(name, metadata.client)
        group.addMultipleNodes(nodes)
        group.addMultipleUser(metadata.client +: users)

        /** Create or get tags id */
        val tids  = tags.map { text =>
          Tag.getIdFromText(text).getOrElse(Tag.createTag(text).tid)
        }
        group.addTags(tids)

        val user = User(metadata.client)
        user.addGroup(group.nid)

        GroupCreateRes(group.nid)
      } else {
        val errorMessage = getClass.getSimpleName
        throw new ActionArgumentException(s"Bad visibility : $visibility in : $errorMessage")
      }
    } else {
      val errorMessage = getClass.getSimpleName
      throw new ActionArgumentException(s"Empty name : $errorMessage")
    }
  }

}

/**
  * [[GroupCreate]] result.
  * @param nid the nid of newly created group
  */
case class GroupCreateRes(nid: NID) extends Result
