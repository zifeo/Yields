package yields.server.mpi.groups

import yields.server.models.NID
import yields.server.mpi.Message

/**
 * Creation of a named group including some nodes.
 * @param name group name
 * @param nodes grouped nodes
 */
case class GroupCreate(name: String, nodes: Seq[NID]) extends Message
