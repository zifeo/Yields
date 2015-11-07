package yields.server.dbi.models

import yields.server.dbi._

/**
 * Represent a image ressource
 * @param nid image id
 *
 * nodes:[nid]:content -> Zset path : pathToContent
 *
 */
class Image private(override val nid: NID) extends Node {

  private var _content: Option[String] = None

  def content: String = _content.getOrElse {
    _content = redis.hget(s"nodes:$nid:content", "path")
    _content.getOrElse(throw new ModelValueNotSetException)
  }

  def content_(newPath: String) = {
    redis.hset(s"nodes:$nid:content", "path", newPath)
  }

}

object Image {
  def createImage(content: String): NID = {
    val lastNid: Long = redis.incr("nodes:nid").getOrElse(-1)
    if (lastNid > 0) {
      new Image(lastNid)
      lastNid
    } else {
      throw new RedisNotAvailableException
    }
  }

  def apply(n: NID): Image = {
    new Image(n)
  }
}