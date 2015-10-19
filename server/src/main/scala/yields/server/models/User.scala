package yields.server.models

import javax.persistence.{Version, Id}

/**
 * Created by jeremy on 18/10/15.
 */
class User {
  @Id var id: String = _
  @Version var version: String = _
  var name: String = _
  var date_creation: java.util.Date = _

  override def toString = "User: " + this.id + ", name: " + this.name
}
