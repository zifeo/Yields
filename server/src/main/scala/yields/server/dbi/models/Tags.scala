package yields.server.dbi.models

import yields.server.dbi._
import com.redis.serialization.Parse.Implicits._
import yields.server.dbi.tags.Tag

/**
  * Tag management for nodes
  */
trait Tags {

  object TagKey {
    val tags: String = s"nodes:$nodeID:tags"
  }

  val nodeID: NID
  private var _tags: Option[Set[TID]] = None

  /** add tags to publisher */
  def addTags(tags: Seq[String]): Unit = {
    tagOrCreate(tags).foreach { x =>
      redis(_.sadd(TagKey.tags, x))
      Tag(x).addNode(nodeID)
    }
  }

  /** remove tags from publisher */
  def remTags(tags: Seq[String]): Unit = {
    tagOrCreate(tags).foreach { x =>
      redis(_.srem(TagKey.tags, x))
      Tag(x).addNode(nodeID)
    }
  }

  /**
    * Get id or create new tag and return id for a list of tags
    * @param tags tags to get id or create
    * @return list of TID corresponding to tags
    */
  def tagOrCreate(tags: Seq[String]): List[TID] = {
    tags.map { text =>
      Tag.getIdFromText(text) match {
        case Some(x) => x
        case None => Tag.create(text).tid
      }
    }.toList
  }

  /** get the tags of a node */
  def tags: Set[String] = {
    val t: Set[TID] = _tags.getOrElse {
      val mem: Option[Set[Option[TID]]] = redis(_.smembers[TID](TagKey.tags))
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
    } yield Tag(tid).text
  }

}
