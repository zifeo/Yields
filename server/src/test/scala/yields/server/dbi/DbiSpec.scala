package yields.server.dbi

import java.io.File

import org.scalatest.{BeforeAndAfter, FlatSpecLike}
import yields.server.utils.Config

/**
  * Flush the database before and after each tests.
  * Database number is already changed (see application.conf in test folder).
  */
private[server] trait DbiSpec extends FlatSpecLike with BeforeAndAfter {

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
