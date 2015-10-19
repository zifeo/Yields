package yields.server

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import yields.server.models.User


import scala.collection.JavaConverters._
import scala.language.{implicitConversions, reflectiveCalls}

object Test extends App {

  implicit def dbWrapper(db: OObjectDatabaseTx) = new {
    def queryBySql[T](sql: String, params: AnyRef*): List[T] = {
      val params4java = params.toArray
      val results: java.util.List[T] = db.query(new OSQLSynchQuery[T](sql), params4java: _*)
      results.asScala.toList
    }
  }

  var uri = "remote:192.168.99.100:32784/Yelds"

  var db: OObjectDatabaseTx = new OObjectDatabaseTx(uri).open("root", "test")

  db.getEntityManager.registerEntityClasses("models")
  println(db.getEntityManager.getRegisteredEntities)

  val user = db.queryBySql[User]("select from user where email = 'hottinger.jeremy@gmail.com'")
  println(user)

}