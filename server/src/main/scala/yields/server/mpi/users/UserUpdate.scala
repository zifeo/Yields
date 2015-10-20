package yields.server.mpi.users

import yields.server.models.{Blob, Email, UID}
import yields.server.mpi.Message

/**
 * Update an user given specific fields.
 * @param uid user id
 * @param email new email address
 * @param name new name
 * @param image new profile image
 */
case class UserUpdate(uid: UID, email: Option[Email], name: Option[String], image: Option[Blob]) extends Message
