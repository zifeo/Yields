package yields.server.dbi.models

import yields.server.dbi._
import com.redis.serialization.Parse.Implicits._

/**
  * Class representing tags and actions on tag
  *
  * Tag database storage :
  * tags:tid    last inserted tid
  * tags:[tid]        Hash -> content = text
  * tags:[tid]:groups set[nid]
  *
  */
final class Tag private(val tid: TID) {

  object TagKey {
    val tag = s"tags:$tid"
    val text = "text"
    val groups = s"$tag:groups"
  }

  private var _text: Option[String] = None

  def text: String = _text.getOrElse {
    _text = redis.withClient(_.hget[String](TagKey.tag, TagKey.text))
    valueOrDefault(_text, "")
  }

  def text_=(text: String): Unit = {
    redis.withClient(_.hset(TagKey.tag, TagKey.text, text))
    _text = Some(text)
  }

  def addNode(nid: NID): Unit = {
    redis.withClient(_.sadd(TagKey.groups, nid))
  }

  def remNode(nid: NID): Unit = {
    redis.withClient(_.srem(TagKey.groups, nid))
  }
}

/** [[Tag]] companion object. */
object Tag {

  object StaticKey {
    val tid = "tags:tid"
    val index = "tags:indexes:tags"
  }

  /** add a tag */
  def create(newTag: String): Tag = {
    val tid = valueOrException(redis.withClient(_.incr(StaticKey.tid)))
    val tag = Tag(tid)
    tag.text = newTag
    redis.withClient(_.hset(StaticKey.index, newTag, tid))
    tag
  }

  def apply(id: TID): Tag = {
    new Tag(id)
  }

  /** get an id corresponding to the tag if it exists, None otherwise */
  def getIdFromText(tag: String): Option[TID] = {
    redis.withClient(_.hget[TID](StaticKey.index, tag))
  }
}