package yields.server.models

import javax.persistence.{Version, Id}

/**
 * Representation of a user
 */
class User {
  @Id var id: String = _
  @Version var version: String = _
  var name: String = _
  var date_creation: java.util.Date = _

  override def toString = "User: " + this.id + ", name: " + this.name + ", creation: " + date_creation
}
