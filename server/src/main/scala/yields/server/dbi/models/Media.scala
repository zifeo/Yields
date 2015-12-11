package yields.server.dbi.models

import java.time.OffsetDateTime

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.MediaException
import yields.server.utils.Temporal

/**
  * Represent a media ressource
  * Medias are stored on the disk under the name created with the hash
  *
  * @param nid media id
  *
  * Special field :
  * nodes:[nid]   -> hash  hash / path / contentType
  *
  */
class Media private(nid: NID) extends Node(nid) {

  object MediaKey {
    val hash = "hash"
    val contentType = "contentType"
    val path = "path"
  }

  private var _filename: Option[String] = None
  private var _contentType: Option[String] = None

  /** Media content getter */
  def content: Blob = {
    // hydrate hash
    if (_filename.isEmpty) {
      filename
    }
    getContentFromDisk(_filename.getOrElse(throw new MediaException("filename doesn't exist")))
      .getOrElse(throw new MediaException("Content doesn't exist on disk"))
  }

  /** Media content setter on disk */
  def content_=(content: Blob) = {
    // hydrate the hash
    if (_filename.isEmpty) {
      filename
    }

    writeContentOnDisk(_filename.getOrElse(throw new MediaException("name is empty")), content)
  }

  /** filename getter */
  def filename: String = _filename.getOrElse {
    _filename = redis(_.hget[String](NodeKey.node, MediaKey.hash))
    valueOrException(_filename)
  }

  /** filename setter */
  private def filename_=(filename: String): Unit = {
    _filename = update(NodeKey.node, MediaKey.hash, filename)
  }

  /** content type getter */
  private def contentType_=(contentType: String): Unit = {
    _contentType = update(NodeKey.node, MediaKey.contentType, contentType)
  }

  /** content type setter */
  def contentType: String = _contentType.getOrElse {
    _contentType = redis(_.hget[String](NodeKey.node, MediaKey.contentType))
    valueOrDefault(_contentType, "")
  }

}

/**
  * Companion object for Media
  */
object Media {

  object ContentType {
    val image = "image"
    val url = "url"
  }

  /**
    * Create a new media from a base64 string content
    * @param content base64 media
    * @return media
    */
  def create(contentType: String, content: Blob, creator: UID): Media = {
    // Create hash
    val media = Media(newIdentity())

    // set values
    media.filename = createHash(content, Temporal.now)
    media.content = content
    media.contentType = contentType
    media.creator = creator
    media.kind = classOf[Media].getSimpleName

    media
  }

  def apply(n: NID): Media = {
    val media = new Media(n)
    media
  }

  /**
    * Create hash from some content
    * @param content content to hash
    * @return hash
    */
  def createHash(content: Blob, date: OffsetDateTime): String = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    val ha = new sun.misc.BASE64Encoder().encode(md.digest(content.getBytes))
    date + "_" + ha.replace('/', '-')
  }

  /**
    * Check if a file exists on disk with full name
    * @param filename name of the file to test (name format: date_hash
    * @return
    */
  def checkFileExist(filename: String): Boolean = {
    models.checkFileExist(filename)
  }

  /** delete a media on disk */
  def deleteContentOnDisk(nid: NID): Unit = {
    models.deleteContentOnDisk(nid)
  }

  /** get path from a filename */
  def buildPathFromName(name: String): String = {
    models.buildPathFromName(name)
  }

}
