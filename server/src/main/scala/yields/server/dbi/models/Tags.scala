package yields.server.dbi.models

import yields.server.dbi._
import com.redis.serialization.Parse.Implicits._

/**
  * Tag management for nodes
  */
trait Tags {

  val NodeTagKey: String
  val nodeID: NID
  private var _tags: Option[Set[TID]] = None

  /** add tags to publisher */
  def addTags(tags: Seq[String]): Unit = {
    getOrCreateTags(tags).foreach { x =>
      redis.withClient(_.sadd(NodeTagKey, x))
      Tag(x).addNode(nodeID)
    }
  }

  /** remove tags from publisher */
  def remTags(tags: Seq[String]): Unit = {
    getOrCreateTags(tags).foreach { x =>
      redis.withClient(_.srem(NodeTagKey, x))
      Tag(x).addNode(nodeID)
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

  /** get the tags of a node */
  def tags: Set[String] = {
    val t: Set[TID] = _tags.getOrElse {
      val mem: Option[Set[Option[TID]]] = redis.withClient(_.smembers[TID](NodeTagKey))
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
