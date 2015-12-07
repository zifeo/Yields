package yields.server.tests

import java.io.File

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import yields.server.dbi.DbiSpec
import yields.server.utils.Config

/**
  * Basic Yields testing specs.
  * Ensure database is empty before/after each tests and that files are empty after each suites.
  */
trait YieldsSpec extends FlatSpec with Matchers with DbiSpec with AllGenerators with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    val folder = new File(Config.getString("ressource.media.folder"))
    if (folder.exists) {
      val files = folder.listFiles
      for (f <- files if f.isFile) {
        f.delete
      }
    }
  }

}