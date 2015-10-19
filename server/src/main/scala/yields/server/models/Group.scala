package yields.server.models

import javax.persistence.{Version, Id}

/**
 * Created by jeremy on 18/10/15.
 */
class Group {
  @Id var id: String = _
  @Version var version: String = _
  var group_name: String = _
  var date_creation: java.util.Date = _
  var last_activity: java.util.Date = _
  var messages: java.util.List[String] = new java.util.ArrayList()

  override def toString = "User: " + this.id
}
