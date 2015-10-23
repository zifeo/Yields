package yields.server.actions.groups

import yields.server.models.GID
import yields.server.actions.Action

/**
 * Message related to a particular group.
 * @param gid group id
 * @param content message content
 */
case class GroupAction(gid: GID, content: String) extends Action
