package yields.server.actions.users

import yields.server.models.UID
import yields.server.actions.Action

/**
 * Lists the groups of the user.
 * @param uid user id
 */
case class UserGroupList(uid: UID) extends Action
