package yields.server.dbi

import java.io.File

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FlatSpec}
import yields.server.dbi.models.Media
import yields.server.utils.Config

/** Flush the database before and after each tests.
  * Database number is already changed (see application.conf in test folder).
  */
trait DBFlatSpec extends FlatSpec with BeforeAndAfter with BeforeAndAfterAll {

  before {
    redis(_.flushdb)
  }

  after {
    redis(_.flushdb)

    deleteDirectory(new File(Config.getString("ressource.media.folder")))

  }

  def deleteDirectory(path: File): Unit = {
    if (path.exists) {
      val files = path.listFiles
      for {
        f <- files
        if f.isFile
      } yield f.delete
    }
  }

}
