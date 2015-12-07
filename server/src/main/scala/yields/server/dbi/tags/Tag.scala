package yields.server.dbi.tags

import yields.server.dbi._
import yields.server.dbi.models._

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

  /** tag text getter */
  def text: String = _text.getOrElse {
    _text = redis(_.hget[String](TagKey.tag, TagKey.text))
    valueOrDefault(_text, "")
  }

  /** tag text setter */
  def text_=(text: String): Unit = {
    redis(_.hset(TagKey.tag, TagKey.text, text))
    _text = Some(text)
  }

  /** link a node to a tag */
  def addNode(nid: NID): Unit = {
    redis(_.sadd(TagKey.groups, nid))
  }

  /** remove a linked node */
  def remNode(nid: NID): Unit = {
    redis(_.srem(TagKey.groups, nid))
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
    val tid = valueOrException(redis(_.incr(StaticKey.tid)))
    val tag = Tag(tid)
    tag.text = newTag
    redis(_.hset(StaticKey.index, newTag, tid))
    tag
  }

  def apply(id: TID): Tag = {
    new Tag(id)
  }

  /** get an id corresponding to the tag if it exists, None otherwise */
  def getIdFromText(tag: String): Option[TID] = {
    redis(_.hget[TID](StaticKey.index, tag))
  }
}