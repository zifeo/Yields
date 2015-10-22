package yields.server

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery

import scala.collection.JavaConverters._
import scala.language.implicitConversions

package object dbi {
  val uri = "remote:192.168.99.100:2424/orientdb"

  lazy implicit val database: OObjectDatabaseTx = new OObjectDatabaseTx(uri).open("root", "AEB2F0FCA414B253A76E235E96856BA436A00E2654B07825867B11060016011D")
  database.getEntityManager.registerEntityClasses("yields.server.models")

  def queryBySql[T](sql: String, params: AnyRef*)(implicit db: OObjectDatabaseTx): List[T] = {
    val params4java = params.toArray
    val results: java.util.List[T] = db.query(new OSQLSynchQuery[T](sql), params4java: _*)
    results.asScala.toList
  }
}
