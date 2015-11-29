package yields.server.dbi.models

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.models.Media._
import yields.server.utils.Config
import scala.io._

/**
  * Represent a media ressource
  * Medias are stored on the disk under the name created with the hash
  *
  * @param nid media id
  *
  *            Special field :
  *            nodes:[nid]   -> hash  hash / path / contentType
  *
  */
class Media private(nid: NID) extends Node(nid) {

  object MediaKey {
    val hash = "hash"
    val contentType = "contentType"
    val path = "path"
  }

  private var _hash: Option[String] = None
  private var _contentType: Option[String] = None
  private var _path: Option[String] = None

  /** Media content getter */
  def content: Blob = {
    // hydrate hash
    if (_hash.isEmpty) {
      hash
    }
    path = _hash.get

    getContentFromDisk(path) match {
      case Some(b) => b
      case None => throw new Exception("Content doesn't exist on disk")
    }
  }

  /** Media content setter on disk */
  def content_=(content: Blob) = {
    // hydrate the hash
    if (_hash.isEmpty) {
      hash
    }
    path = _hash.get

    if (_path.isEmpty)
      throw new Exception("Cannot write in non-existent path")

    writeContentOnDisk(_path.get, content)

  }

  def hash: String = _hash.getOrElse {
    _hash = redis(_.hget[String](NodeKey.node, MediaKey.hash))
    valueOrException(_hash)
  }

  /** Store the hash in the database to easily retrieve the content from the disk */
  private def hash_=(hash: String): Unit = {
    redis(_.hset(NodeKey.node, MediaKey.hash, hash))
    _hash = Some(hash)
    path = _hash.get
  }

  private def contentType_=(contentType: String): Unit = {
    redis(_.hset(NodeKey.node, MediaKey.contentType, contentType))
    _contentType = Some(contentType)
  }

  def contentType: String = _contentType.getOrElse {
    _contentType = redis(_.hget[String](NodeKey.node, MediaKey.contentType))
    valueOrException(_contentType)
  }

  def path: String = _path.getOrElse {
    _path = redis(_.hget[String](NodeKey.node, MediaKey.path))
    valueOrException(_path)
  }

  private def path_=(hash: String): Unit = {
    val path = buildPathFromName(hash)
    redis(_.hset(NodeKey.node, MediaKey.path, path))
    _path = Some(path)
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
    val media = Media(newIdentity())

    // set values
    media.hash = createHash(content)
    media.content = content
    media.contentType = contentType
    media.creator = creator
    media.kind = this.getClass.getSimpleName

    media
  }

  def apply(n: NID): Media = {
    val media = new Media(n)
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

  def deleteContentOnDisk(nid: NID): Unit = {
    val media = Media(nid)
    val file = new File(media.path)
    if (file.exists) {
      file.delete()
    }
  }

}
