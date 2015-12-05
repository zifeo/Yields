package yields.server.dbi.models

import yields.server.dbi._
import com.redis.serialization.Parse.Implicits._

/**
  * Represents a publisher
  *
  * A publisher is a kind of node that is added to other private nodes
  * When a user publish in one of this special node it's spread into each
  * node that registered it as a publisher
  *
  * Some fields used from the Node class see their purpose change a bit
  * List[UID]     -> List of user that can publish in it
  * List[NID]     -> List of nodes that registered it
  */
abstract class AbstractPublisher protected(nid: NID) extends Node(nid) {

  object PublisherKey {
    val node = NodeKey.node
    val tags = s"$node:tags"
  }

  private var _tags: Option[Set[TID]] = None

  /** Add message */
  override def addMessage(content: FeedContent): Boolean = {
    val done = super.addMessage(content)
    if (done) {
      broadcast((content._1, nid, content._3, content._4))
    }
    done
  }

  private def broadcast(content: FeedContent): Unit = {
    for {
      nid <- nodes
      group = Group(nid)
    } yield group.addMessage(content)
  }

  /** add tags to publisher */
  def addTags(tags: Seq[String]): Unit = {

    /** Create or get tags id */
    getOrCreateTags(tags).foreach { x =>
      redis.withClient(_.sadd(PublisherKey.tags, x))
      val t = Tag(x)
      t.addNode(nid)
    }
  }

  def remTags(tags: Seq[String]): Unit = {
    getOrCreateTags(tags).foreach { x =>
      redis.withClient(_.srem(PublisherKey.tags, x))
      val t = Tag(x)
      t.addNode(nid)
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
    val t: Set[TID] = _tags.getOrElse {
      val mem: Option[Set[Option[TID]]] = redis.withClient(_.smembers[TID](PublisherKey.tags))
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
