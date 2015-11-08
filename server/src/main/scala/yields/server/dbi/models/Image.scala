package yields.server.dbi.models

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.{UnincrementableIdentifierException, IllegalValueException}

/**
 * Represent a image ressource
 * @param nid image id
 *
 * Special field :
 * nodes:[nid]:content -> Zset path : pathToContent
 *
 */
class Image private(override val nid: NID) extends Node {

  object Key {
    val content = s"$nodes:content"
  }

  private var _content: Option[String] = None

  /** Image content getter */
  def content: String = _content.getOrElse {
    _content = redis.withClient(_.get[String](Key.content))
    _content.getOrElse(throw new IllegalValueException(s"image content should be have a value"))
  }

  /** Image content setter */
  def content_=(newPath: String) = {
    redis.withClient(_.set(Key.content, newPath))
  }

}

/**
 * Companion object for Image
 */
object Image {

  /**
   * Create a new image from a path
   * @param content path to image
   * @return the newly created image
   */
  def createImage(content: String): Image = {
    Image(Node.newNID())
  }

  def apply(n: NID): Image = {
    new Image(n)
  }

}
