package yields.server.actions.users

import spray.json.JsonFormat
import yields.server.models.{Blob, Email, UID}
import yields.server.actions.Action
import spray.json.DefaultJsonProtocol._

/**
 * Update an user given specific fields.
 * @param uid user id
 * @param email new email address
 * @param name new name
 * @param image new profile image
 */
case class UserUpdate(uid: UID, email: Option[Email], name: Option[String], image: Option[Blob]) extends Action