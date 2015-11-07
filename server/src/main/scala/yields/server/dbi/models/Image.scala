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
    val content = s"$node:content"
  }

  private var _content: Option[String] = None

  def content: String = _content.getOrElse {
    _content = redis.get[String](Key.content)
    _content.getOrElse(throw new ModelValueNotSetException)
  }

  def content_=(newPath: String) = {
    redis.set(Key.content, newPath)
  }

}

object Image {

  def createImage(content: String): Image = {
    val nid = redis.incr("nodes:nid").getOrElse(throw new UnincrementalIdentifier)
    new Image(nid)
  }

  def apply(n: NID): Image = {
    new Image(n)
  }

}
