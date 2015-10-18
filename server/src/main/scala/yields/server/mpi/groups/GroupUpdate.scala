package yields.server.mpi.groups

import yields.server.models.{Blob, GID}
import yields.server.mpi.Message

/**
 * Update a group given given specific fields.
 * @param gid group id
 * @param name new name
 * @param pic new profile image
 */
case class GroupUpdate(gid: GID, name: Option[String], pic: Option[Blob]) extends Message
