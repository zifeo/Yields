package yields.server.mpi.users

import yields.server.models.Email
import yields.server.mpi.Message

/**
 * Connects an user to the server.
 * @param email user mail
 */
case class UserConnect(email: Email) extends Message
