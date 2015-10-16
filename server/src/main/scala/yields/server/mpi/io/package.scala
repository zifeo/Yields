package yields.server.mpi

import spray.json.DefaultJsonProtocol._

/**
 * Provides simple json format that do not requires a full read/write specification.
 */
package object io {

  implicit val groupMessageJsonFormat = jsonFormat2(GroupMessage)
  implicit val userUpdateJsonFormat = UserUpdateJsonFormat

}
