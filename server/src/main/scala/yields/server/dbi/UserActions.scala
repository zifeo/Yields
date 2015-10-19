package yields.server.dbi

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.id.ORecordId
import yields.server.models.User

/**
 * Created by jeremy on 18/10/15.
 */
class UserActions {
  var uri = "remote:127.0.0.1/orientdbtest"

  var db: OObjectDatabaseTx = new OObjectDatabaseTx(uri).open("root", "test")
  db.getEntityManager.registerEntityClasses("User")


  def getUserByEmail(email: String):User = {
    val user = db.queryBySql[User]("select from user where email = ?", email)
    user.head
  }

  def getUserByRid(rid: String):User = {
    val user = db.queryBySql[User]("select from user where @rid = ?", new ORecordId(rid))
    user.head
  }

  def createUser(name: String, email: String): User = {
    val user = db.queryBySql[User]("insert into user Set date_creation = '2015-10-17', email = ?, name = ?", name, email)
    user.head
  }

  def addUserToGroup(ridUser: String, ridGroup: String) = {
    db.queryBySql("create edge from ? to ?", new ORecordId(ridUser), new ORecordId(ridGroup))
  }

  def getGroupsFromUser(ridUser: String) = {
    db.queryBySql[User]("select from E where out = ?", new ORecordId(ridUser))
  }

}
