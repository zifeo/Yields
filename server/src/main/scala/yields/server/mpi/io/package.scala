package yields.server.mpi

import spray.json.DefaultJsonProtocol._

/**
 * Provides json format for read/write specification.
 */
package object io {

  // Do not forget to add message subclass format to message format
  implicit val messageJsonFormat = MessageJsonFormat

  implicit val groupMessageJsonFormat = jsonFormat2(GroupMessage)
  implicit val userUpdateJsonFormat = jsonFormat4(UserUpdate)

}
