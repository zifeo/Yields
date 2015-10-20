package yields.server.mpi.users

import yields.server.models.UID
import yields.server.mpi.Message

/**
 * Lists the groups of the user.
 * @param uid user id
 */
case class UserGroupList(uid: UID) extends Message
