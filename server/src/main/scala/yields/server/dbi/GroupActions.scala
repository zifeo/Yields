package yields.server.dbi

import java.text.{SimpleDateFormat, DateFormat}
import java.util.Date

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.id.ORecordId
import yields.server.models.{Group, User}

/**
 * Actions to do on the db about groups
 */
class GroupActions {
  var uri = "remote:127.0.0.1/orientdbtest"

  var db: OObjectDatabaseTx = new OObjectDatabaseTx(uri).open("root", "test")
  db.getEntityManager.registerEntityClasses("Group")

  val dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val date: String = dateFormat.format(new Date())

  def createGroup(name: String): Group = {
    db.queryBySql("insert into group Set date_creation = ?, group_name = ?", date, name).head
  }

  def getUsersFromGroup(ridGroup: String): List[User] = {
    db.queryBySql("select from E where in = ?", new ORecordId(ridGroup))
  }

  def addMessage(ridGroup: String, ridSender: String, body: String) = {
    db.queryBySql("update ? ADD messages = '{\"sender\":\"?\", \"time\":?, \"body\":\"?\"}'", new ORecordId(ridGroup), new ORecordId(ridSender), date, body)
  }

  def getGroupInfos(ridGroup: String): Group = {
    db.queryBySql("select from ?", new ORecordId(ridGroup)).head
  }
}
