package yields.server.dbi

import java.text.{SimpleDateFormat, DateFormat}
import java.util.Date

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.id.ORecordId
import yields.server.models.{Group, User}
import scala.language.reflectiveCalls
import scala.language.implicitConversions

/**
 * Actions to do on the db about groups
 */
object GroupActions {
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
