package yields.server.mpi.groups

import yields.server.models.GID
import yields.server.mpi.Message

/**
 * Message related to a particular group.
 * @param gid group id
 * @param content message content
 */
case class GroupMessage(gid: GID, content: String) extends Message
