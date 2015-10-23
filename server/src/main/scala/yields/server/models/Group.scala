package yields.server.models

import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date
import javax.persistence.{Id, Version}

import com.orientechnologies.orient.core.id.ORecordId
import yields.server.dbi._

/**
 * Representation of a group
 */
final class Group {
  @Id var id: String = _
  @Version var version: String = _
  var group_name: String = _
  var date_creation: java.util.Date = _
  var last_activity: java.util.Date = _
  var messages: java.util.List[String] = new java.util.ArrayList()

  override def toString = s"Group: $id, name: $group_name , creation: $date_creation, last activity: $last_activity"
}

object Group {
  val dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val date: String = dateFormat.format(new Date())

  def createGroup(name: String): Group = {
    val res = queryBySql("insert into group Set date_creation = ?, group_name = ?", date, name)
    res match {
      case Nil => throw new NoSuchElementException
      case head :: tail => res.head
    }
  }

  def getUsersFromGroup(ridGroup: String): List[User] = {
    queryBySql("select from E where in = ?", new ORecordId(ridGroup))
  }

  def addMessage(ridGroup: String, ridSender: String, body: String) = {
    queryBySql("""update ? ADD messages = '{"sender":"?", "time":?, "body":"?"}'""", new ORecordId(ridGroup), new ORecordId(ridSender), date, body)
  }

  def getGroupInfos(ridGroup: String): Group = {
    val res = queryBySql("select from ?", new ORecordId(ridGroup))
    res match {
      case Nil => throw new NoSuchElementException
      case head :: tail => res.head
    }
  }
}