package yields.server.mpi

import yields.server.models.GID

/**
 * Message related to a particular group.
 * @param gid group id
 * @param content message content
 */
case class GroupMessage(gid: GID, content: String) extends Message
