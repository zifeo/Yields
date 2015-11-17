package yields.server.dbi.models

import java.io.PrintWriter

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.{UnincrementableIdentifierException, IllegalValueException}
import yields.server.dbi.models.Image._
import yields.server.dbi.models.Node._

/**
  * Represent a image ressource
  *
  * Images are stored on the disk under the name created with the hash
  *
  * @param nid image id
  *
  *            Special field :
  *            nodes:[nid]:hash -> set hash
  *
  */
class Image private(override val nid: NID) extends Node {

  object ImageKey {
    val hash = s"${NodeKey.node}:hash"
  }

  private val _hash: String = ""

  /** Image content getter */
  def content: String = {
    import scala.io._
    val source = Source.fromFile(s"$_hash.bin")
    val lines = try source.mkString finally source.close()
    lines
  }

  /** Image content setter on disk */
  def content_=(content: String) = {
    new PrintWriter(s"$_hash.bin") {
      write(content)
    }
  }

  /** Store the hash in the database to easily retrieve the content from the disk */
  private def setHash(hash: String) = {
    redis.withClient(_.set(ImageKey.hash, hash))
  }

}

/**
  * Companion object for Image
  */
object Image {

  /**
    * Create a new image from a base64 string content
    * @param content base64 image
    * @return image
    */
  def createImage(content: String): Image = {
    // Create hash
    val hash = createHash(content)
    val img = Image(Node.newNID())

    // set values
    img.setHash(hash)
    img.content = content

    img
  }

  def apply(n: NID): Image = {
    new Image(n)
  }

  private def createHash(content: String): String = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    val ha = new sun.misc.BASE64Encoder().encode(md.digest(content.getBytes))
    ha
  }

}
