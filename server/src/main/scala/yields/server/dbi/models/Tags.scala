package yields.server.dbi.models

import yields.server.dbi._
import com.redis.serialization.Parse.Implicits._

trait Tags {

  val nidTag: NID
  private var _tags: Option[Set[TID]] = None

  /** add tags to publisher */
  def addTags(tags: Seq[String]): Unit = {

    // TODO find better way to pass nid and store key
    val key = s"nodes:$nidTag:tags"

    /** Create or get tags id */
    getOrCreateTags(tags).foreach { x =>
      redis.withClient(_.sadd(key, x))
      val t = Tag(x)
      t.addNode(nidTag)
    }
  }

  def remTags(tags: Seq[String]): Unit = {
    // TODO find better way to pass nid and store key
    val key = s"nodes:$nidTag:tags"

    getOrCreateTags(tags).foreach { x =>
      redis.withClient(_.srem(key, x))
      val t = Tag(x)
      t.addNode(nidTag)
    }
  }

  /**
    * Get id or create new tag and return id for a list of tags
    * @param tags tags to get id or create
    * @return list of TID corresponding to tags
    */
  def getOrCreateTags(tags: Seq[String]): List[TID] = {
    tags.map { text =>
      Tag.getIdFromText(text) match {
        case Some(x) => x
        case None => Tag.create(text).tid
      }
    }.toList
  }

  /** get the tags of a publisher */
  def tags: Set[String] = {
    // TODO find better way to pass nid and store key
    val key = s"nodes:$nidTag:tags"

    val t: Set[TID] = _tags.getOrElse {
      val mem: Option[Set[Option[TID]]] = redis.withClient(_.smembers[TID](key))
      val t: Set[TID] = mem match {
        case Some(x: Set[Option[TID]]) =>
          val defined: Set[Option[TID]] = x.filter(_.isDefined)
          val noOpt: Set[TID] = defined.map(_.get)
          noOpt
        case _ => Set()
      }
      _tags = Some(t)
      t.toSet
    }

    for {
      tid <- t
      tag = Tag(tid)
    } yield tag.text
  }

}
