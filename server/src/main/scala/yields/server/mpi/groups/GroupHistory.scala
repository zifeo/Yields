package yields.server.mpi.groups

import yields.server.models.{DateTime, GID}
import yields.server.mpi.Message

/**
 * Fetch each group message between two dates with time.
 * @param gid group id
 * @param from start date
 * @param to end date
 */
case class GroupHistory(gid: GID, from: DateTime, to: DateTime) extends Message
