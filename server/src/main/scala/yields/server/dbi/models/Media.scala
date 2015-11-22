package yields.server.dbi.models

import java.io.{File, PrintWriter}
import java.nio.file.{Paths, Files}

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.{UnincrementableIdentifierException, IllegalValueException}
import yields.server.dbi.models.Media._
import yields.server.dbi.models.Node._
import yields.server.utils.Config

/**
  * Represent a media ressource
  *
  * Medias are stored on the disk under the name created with the hash
  *
  * @param nid mdeia id
  *
  *            Special field :
  *            nodes:[nid]:hash -> set hash
  *
  */
class Media private(override val nid: NID) extends Node {

  object MediaKey {
    val hash = s"${NodeKey.node}:hash"
    val contentType = s"${NodeKey.node}:contentType"
  }

  private var _hash: Option[String] = None
  private var _contentType: Option[String] = None
  private var _path: Option[String] = None

  /** Media content getter */
  def content: Blob = {
    import scala.io._
    if (_path.isDefined && _hash.isDefined) {
      if (checkFileExist(_hash.get)) {
        val p = _path.get
        val source = Source.fromFile(s"$p")
        val lines = try source.mkString finally source.close()
        lines
      } else {
        throw new Exception("Content doesnt exist on disk")
      }
    } else {
      throw new Exception("Cannot read from non-existent path")
    }
  }

  /** Media content setter on disk */
  def content_=(content: Blob) = {
    if (_path.isDefined) {
      val p = _path.get
      val file = new File(p)
      if (!checkFileExist(_hash.get)) {
        file.getParentFile.mkdirs
        file.createNewFile()
      }

      if (checkFileExist(_hash.get)) {
        val pw = new PrintWriter(new File(p))
        pw.write(content)
        pw.close()
      } else {
        throw new Exception("Error creating the file on disk")
      }
    } else {
      throw new Exception("Cannot write in non-existent path")
    }
  }

  /** Store the hash in the database to easily retrieve the content from the disk */
  private def hash_=(hash: String) = {
    redis.withClient(_.set(MediaKey.hash, hash))
    _hash = Some(hash)
    _path = Some(buildPathFromName(_hash.get))
  }

  private def contentType_=(contentType: String) = {
    redis.withClient(_.set(MediaKey.contentType, contentType))
    _contentType = Some(contentType)
  }

  def contentType: String = {
    _contentType = redis.withClient(_.get[String](MediaKey.contentType))
    valueOrDefault(_contentType, "")
  }

  def hash: String = {
    _hash = redis.withClient(_.get[String](MediaKey.hash))
    valueOrDefault(_hash, "")
  }

  def path: String = valueOrDefault(_path, "")

  private def path_=(hash: String) = {
    _path = Some(buildPathFromName(hash))
  }

}

/**
  * Companion object for Media
  */
object Media {

  /**
    * Create a new media from a base64 string content
    * @param content base64 media
    * @return media
    */
  def createMedia(contentType: String, content: Blob, creator: UID): Media = {
    // Create hash
    val hash = createHash(content)
    val media = Media(Node.newNID())

    // set values
    media.hash = hash
    media.content = content
    media.contentType = contentType
    media.creator = creator

    media
  }

  def apply(n: NID): Media = {
    val media = new Media(n)
    media.path = media.hash
    media
  }

  def createHash(content: Blob): String = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    val ha = new sun.misc.BASE64Encoder().encode(md.digest(content.getBytes))
    ha.filter(_ != '/')
  }

  def checkFileExist(name: String): Boolean = {
    Files.exists(Paths.get(buildPathFromName(name)))
  }

  def buildPathFromName(name: String): String = {
    Config.getString("ressource.media.folder") + name + Config.getString("ressource.media.extension")
  }

}