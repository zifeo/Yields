package yields.server.models

import javax.persistence.{Id, Version}

/**
 * Representation of a group
 */
class Group {
  @Id var id: String = _
  @Version var version: String = _
  var group_name: String = _
  var date_creation: java.util.Date = _
  var last_activity: java.util.Date = _
  var messages: java.util.List[String] = new java.util.ArrayList()

  override def toString = "Group: " + this.id + ", name: " + group_name + ", creation: " + date_creation + ", last activity: " + last_activity
}
