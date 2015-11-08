package yields.server.dbi.models

import com.redis.serialization.Parse.Implicits._
import yields.server.dbi._
import yields.server.dbi.exceptions.{UnincrementalIdentifier, RedisNotAvailableException, ModelValueNotSetException}

/**
 * Represent a image ressource
 * @param nid image id
 *
 * nodes:[nid]:content -> Zset path : pathToContent
 *
 */
class Image private(override val nid: NID) extends Node {

  object Key {
    val content = s"$nodes:content"
  }

  private var _content: Option[String] = None

  def content: String = _content.getOrElse {
    _content = redis.withClient(_.get[String](Key.content))
    _content.getOrElse(throw new ModelValueNotSetException)
  }

  def content_=(newPath: String) = {
    redis.withClient(_.set(Key.content, newPath))
  }

}

object Image {

  def createImage(content: String): Image = {
    Image(Node.newNID())
  }

  def apply(n: NID): Image = {
    new Image(n)
  }

}