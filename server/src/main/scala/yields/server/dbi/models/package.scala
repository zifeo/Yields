package yields.server.dbi

import java.io.{PrintWriter, File}
import java.nio.file.{Paths, Files}
import java.time.OffsetDateTime

import com.redis.serialization.{Format, Parse}
import yields.server.dbi.exceptions.MediaException
import yields.server.utils.Config

import scala.io.Source

/**
  * Short models types and some formats.
  */
package object models {

  type Identity = Long

  /** Represents an user identifier. */
  type UID = Identity

  /** Represents a node identifier. */
  type NID = Identity

  /** Represent a tag identifier */
  type TID = Long

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = String

  /** Represents a feed content (or so called message). */
  type FeedContent = (OffsetDateTime, Identity, Option[NID], String)

  import Parse.Implicits._

  val resourcesFolder = Config.getString("resources.media.folder")
  val resourcesExt = Config.getString("resources.media.extension")

  /** Redis format for [[FeedContent]], [[OffsetDateTime]].  */
  implicit val formatFeedContent = Format {
    case (datetime, uid, Some(nid), text) => s"$datetime,$uid,$nid,$text"
    case (datetime, uid, None, text) => s"$datetime,$uid,,$text"
    case datetime: OffsetDateTime => datetime.toString
  }

  /** [[OffsetDateTime]] Redis parser. */
  implicit val parseOffsetDateTime = Parse[OffsetDateTime](byteArray => OffsetDateTime.parse(byteArray))

  /** [[FeedContent]] Redis parser. */
  implicit val parseFeedContent = Parse[FeedContent] { byteArray =>
    byteArray.split(",", 4) match {
      case Array(datetime, uid, "", text) =>
        (OffsetDateTime.parse(datetime), uid.toLong, None, text)

      case Array(datetime, uid, nid, text) =>
        (OffsetDateTime.parse(datetime), uid.toLong, Some(nid.toLong), text)
    }
  }

  /**
    * write some blob on disk
    * @param filename filename to write at
    * @param content blob to write
    *
    *                TODO security verification, open door to everyone who wants to write on disk
    */
  def writeContentOnDisk(filename: String, content: Blob): Unit = {
    val path = buildPathFromName(filename)
    val file = new File(path)
    if (!checkFileExist(path)) {
      file.getParentFile.mkdirs
      file.createNewFile()
    }

    if (!checkFileExist(path))
      throw new MediaException("Error creating the file on disk")

    val pw = new PrintWriter(file)
    pw.write(content.toCharArray)
    pw.close()
  }

  /**
    * get some content from disk
    * @param filename path to content to get
    * @return blob content
    *
    *         TODO security verification, open door to everyone who wants to get content from the disk
    */
  def getContentFromDisk(filename: String): Option[Blob] = {
    val path = buildPathFromName(filename)
    if (checkFileExist(path)) {
      val source = Source.fromFile(s"$path")
      val lines = try source.mkString finally source.close()
      Some(lines.toCharArray.map(_.toByte))
    } else {
      None
    }
  }

  /**
    * Check if a file exists on disk with full name
    * @param path path of the file to test (name format: date_hash
    * @return
    */
  def checkFileExist(path: String): Boolean = {
    Files.exists(Paths.get(path))
  }

  /**
    * Build a path from a file name
    * @param name filename
    * @return path
    */
  def buildPathFromName(name: String): String = {
    s"$resourcesFolder/$name.$resourcesExt"
  }

  /**
    * Delete a file on disk
    * @param nid
    */
  def deleteContentOnDisk(nid: NID): Unit = {
    val media = Media(nid)
    val file = new File(buildPathFromName(media.filename))
    if (file.exists) {
      file.delete()
    }
  }

}
