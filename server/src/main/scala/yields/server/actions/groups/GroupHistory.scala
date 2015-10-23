package yields.server.actions.groups

import yields.server.models.{DateTime, GID}
import yields.server.actions.Action

/**
 * Fetch each group message between two dates with time.
 * @param gid group id
 * @param from start date
 * @param to end date
 */
case class GroupHistory(gid: GID, from: DateTime, to: DateTime) extends Action
