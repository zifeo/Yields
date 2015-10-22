package yields.server.dbi

import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.id.ORecordId
import yields.server.models.{Group, User}
import scala.language.reflectiveCalls

/**
 * Actions to do on the db about users
 */
object UserActions {

  // Create a date for now
  val dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val date: String = dateFormat.format(new Date())

  def getUserByEmail(email: String): User = {
    val user = queryBySql[User]("select from user where email = ?", email)
    user match {
      case Nil => throw new NoSuchElementException
      case head :: tail => user.head
    }
  }

  def getUserByRid(rid: String): User = {
    val user = queryBySql[User]("select from user where @rid = ?", new ORecordId(rid))
    user match {
      case Nil => throw new NoSuchElementException
      case head :: tail => user.head
    }
  }

  def createUser(name: String, email: String): User = {
    val user = queryBySql[User]("insert into user Set date_creation = ?, email = ?, name = ?", date, name, email)
    user match {
      case Nil => throw new NoSuchElementException
      case head :: tail => user.head
    }
  }

  def addUserToGroup(ridUser: String, ridGroup: String): Unit = {
    queryBySql("create edge from ? to ?", new ORecordId(ridUser), new ORecordId(ridGroup))
  }

  def getGroupsFromUser(ridUser: String):List[Group] = {
    queryBySql[Group]("select from E where out = ?", new ORecordId(ridUser))
  }
}
