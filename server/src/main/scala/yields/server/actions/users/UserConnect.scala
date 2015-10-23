package yields.server.actions.users

import yields.server.models.Email
import yields.server.actions.Action

/**
 * Connects an user to the server.
 * @param email user mail
 */
case class UserConnect(email: Email) extends Action
