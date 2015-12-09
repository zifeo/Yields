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
    tags.foreach(Indexes.searchableRegister(_, nodeID))
  }

  /** remove tags from publisher */
  def remTags(tags: Seq[String]): Unit = {
    tagOrCreate(tags).foreach { x =>
      redis(_.srem(TagKey.tags, x))
      Tag(x).addNode(nodeID)
    }
    tags.foreach(Indexes.searchableUnregister(_, nodeID))
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
    val t = _tags.getOrElse {
      val mem = redis(_.smembers[TID](TagKey.tags))
      val tags: Set[NID] = mem match {
        case Some(matches) => matches.flatten
        case _ => Set.empty
      }
      _tags = Some(tags)
      tags
    }

    for {
      tid <- t
    } yield Tag(tid).text
  }

}