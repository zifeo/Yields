package yields.server

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery

import scala.collection.JavaConverters._
import scala.language.implicitConversions

package object dbi {
  val uri = "remote:192.168.99.100:2424/orientdb"

  lazy implicit val database: OObjectDatabaseTx = new OObjectDatabaseTx(uri).open("root", "test")
  database.getEntityManager.registerEntityClasses("yields.server.models")

  def queryBySql[T](sql: String, params: AnyRef*)(implicit db: OObjectDatabaseTx): List[T] = {
    val params4java = params.toArray
    val results: java.util.List[T] = db.query(new OSQLSynchQuery[T](sql), params4java: _*)
    results.asScala.toList
  }

  // implicit val dbInstance = {
  /* def executeAsync(osql: String, params: Map[String, String] = Map()): Future[List[ODocument]] = {
      import scala.concurrent._
      val p = promise[List[ODocument]]
      val f = p.future
      val req: OCommandRequest = database.command(
        new OSQLAsynchQuery[ODocument]("select * from animal where name = 'Gipsy'",
          new OCommandResultListener() {
            var acc = List[ODocument]()

            @Override
            def result(iRecord: Any): Boolean = {
              val doc = iRecord.asInstanceOf[ODocument]
              acc = doc :: acc
              true
            }

            @Override
            def end() {
              p.success(acc)
            }
          }))
      req.execute()
      f
    } */
  // }
}
