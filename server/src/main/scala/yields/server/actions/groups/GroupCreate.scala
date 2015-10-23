package yields.server.actions.groups

import yields.server.models.NID
import yields.server.actions.Action

/**
 * Creation of a named group including some nodes.
 * @param name group name
 * @param nodes grouped nodes
 */
case class GroupCreate(name: String, nodes: Seq[NID]) extends Action
