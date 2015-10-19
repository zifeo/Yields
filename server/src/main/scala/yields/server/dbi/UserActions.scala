package yields.server.dbi

import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.id.ORecordId
import yields.server.models.{Group, User}

/**
 * Actions to do on the db about users
 */
class UserActions {
  var uri = "remote:127.0.0.1/orientdbtest"

  var db: OObjectDatabaseTx = new OObjectDatabaseTx(uri).open("root", "test")
  db.getEntityManager.registerEntityClasses("User")

  // Create a date for now
  val dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val date: String = dateFormat.format(new Date())

  def getUserByEmail(email: String): User = {
    val user = db.queryBySql[User]("select from user where email = ?", email)
    user.head
  }

  def getUserByRid(rid: String): User = {
    val user = db.queryBySql[User]("select from user where @rid = ?", new ORecordId(rid))
    user.head
  }

  def createUser(name: String, email: String): User = {
    val user = db.queryBySql[User]("insert into user Set date_creation = ?, email = ?, name = ?", date, name, email)
    user.head
  }

  def addUserToGroup(ridUser: String, ridGroup: String) = {
    db.queryBySql("create edge from ? to ?", new ORecordId(ridUser), new ORecordId(ridGroup))
  }

  def getGroupsFromUser(ridUser: String) = {
    db.queryBySql[Group]("select from E where out = ?", new ORecordId(ridUser))
  }
}
