package yields.server.dbi.tags

import yields.server.dbi.models.{TagContent, TID, NID}


/**
  * Class representing tags and actions on tag
  *
  * Tag database storage :
  * tags:tid    last inserted tid
  * tags:[tid]  Hash -> content / groups
  *
  * tags:indexes:content
  *
  */
final class Tag private(val tid: TID) {

  object TagKey {
    val tag = s"tags:$tid"
    val content = "content"
    val groups = "groups"
  }

  def addGroup(nid: NID): Unit = {

  }

}

object Tag {
  var _tag: Option[Tag] = None

  /** add a tag */
  def createTag(newTag: TagContent): TID = {
    ???
  }

  /** get tag corresponding to a pattern */
  def getTagsFromPattern(pattern: String): List[TagContent] = {
    ???
  }

  /** get tag from TID if it exist, None otherwise */
  def apply(id: TID): Tag = {
    new Tag(id)
  }

  /** get an id corresponding to the tag if it exists, None otherwise */
  def getIdFromTag(tag: TagContent): Option[TID] = {
    ???
  }
}
