package yields.server.mpi

import yields.server.models.{Blob, Email, UID}

/**
 * User update given specific fields.
 * @param uid user id
 * @param email new email address
 * @param name new name
 * @param image new profile image
 */
case class UserUpdate(uid: UID, email: Option[Email], name: Option[String], image: Option[Blob]) extends Message
