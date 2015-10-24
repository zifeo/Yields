package yields.server.models

import java.text.{SimpleDateFormat, DateFormat}
import java.util.{Objects, Date}
import javax.persistence.{Version, Id}

import com.orientechnologies.orient.core.id.ORecordId
import yields.server.dbi._

/**
 * Representation of a user
 */
final class User {
  @Id var id: String = _
  @Version var version: String = _
  var name: String = _
  var date_creation: java.util.Date = _

  override def toString = s"User: $id, name: $name, creation: $date_creation"
}


object User {

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
    val user = queryBySql[User]("insert into user Set date_creation = ?, email = ?, name = ?", databaseDateTime,
    name, email)
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