package yields.server

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import yields.server.utils.Config

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import scala.language.implicitConversions

package object dbi {

  private[dbi] lazy implicit val database: OObjectDatabaseTx = {
    val db = new OObjectDatabaseTx(Config.getString("database.uri"))
    db.open(Config.getString("database.user"), Config.getString("database.pass"))
    db.getEntityManager.registerEntityClasses("yields.server.dbi.models")
    db
  }

  def queryBySql[T](sql: String, params: AnyRef*)(implicit db: OObjectDatabaseTx): List[T] = {
    val params4java = params.toArray
    val results: java.util.List[T] = db.query(new OSQLSynchQuery[T](sql), params4java: _*)
    results.asScala.toList
  }

}
