package yields.server.dbi

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import yields.server.models.{User, Group}

/**
 * Created by jeremy on 19/10/15.
 */
class GroupActions {
  var uri = "remote:127.0.0.1/orientdbtest"

  var db: OObjectDatabaseTx = new OObjectDatabaseTx(uri).open("root", "test")
  db.getEntityManager.registerEntityClasses("Group")

  def createGroup(name: String): Group = {
    db.queryBySql("insert into group Set date_creation = '2015-10-17', group_name = ?", name).head
  }

  def getUsersFromGroup(ridGroup: String): List[User] = {
    db.queryBySql("select from E where in = ?", new ORecordId(ridGroup))
  }

  def addMessage(ridGroup: String, ridSender: String, body: String) = {
    db.queryBySql("update ? ADD messages = '{\"sender\":\"?\", \"time\":2015-10-18, \"body\":\"?\"}'", new ORecordId(ridGroup), new ORecordId(ridSender), body)
  }

  def getGroupInfos(ridGroup: String):Group = {
    db.queryBySql("select from ?", new ORecordId(ridGroup))
  }
}
