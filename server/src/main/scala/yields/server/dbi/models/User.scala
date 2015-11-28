package yields.server.dbi.models

import java.time.OffsetDateTime

import com.redis.serialization.Parse.Implicits._
import yields.server.actions.exceptions.{NewUserExistException, UnauthorizedActionException}
import yields.server.dbi._
import yields.server.dbi.exceptions.IllegalValueException
import yields.server.dbi.models.Node.StaticNodeKey
import yields.server.dbi.models.User.StaticKey
import yields.server.utils.Temporal

/**
  * User model with linked database interface.
  *
  * Each attribute can be load separately on its own call to improve performance.
  * However work involving many of those, should manually call [[hydrate()]] to load all data.
  * Attributes new values are saved on respective setters.
  *
  * Database structure:
  * users:[uid] Map[String, String] - name / email / pic / created_at / updated_at / connected_at
  * users:[uid]:groups Map[NID, OffsetDateTime]
  * users:[uid]:entourage Map[UID, OffsetDateTime]
  * users:indexes:email Map[Email, UID] - email
  *
  * TODO: improve setters by only settings if the value is different.
  *
  * @param uid user id
  */
final class User private (val uid: UID) {

  object Key {
    val user = s"users:$uid"
    val groups = s"$user:groups"
    val entourage = s"$user:entourage"
  }

  private var _name: Option[String] = None
  private var _email: Option[Email] = None
  private var _pic: Option[Blob] = None
  private var _created_at: Option[OffsetDateTime] = None
  private var _updated_at: Option[OffsetDateTime] = None
  private var _connected_at: Option[OffsetDateTime] = None

  private var _groups: Option[List[NID]] = None
  private var _entourage: Option[List[UID]] = None

  /** Name getter. */
  def name: String = _name.getOrElse {
    _name = redis(_.hget[String](Key.user, StaticKey.name))
    valueOrDefault(_name, "")
  }

  /** Name setter. */
  def name_=(newName: String): Unit =
    _name = update(StaticKey.name, newName)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (StaticKey.updated_at, Temporal.now))
    redis(_.hmset(Key.user, updates))
    Some(value)
  }

  /** Email getter. */
  def email: Email = _email.getOrElse {
    _email = redis(_.hget[Email](Key.user, StaticKey.email))
    valueOrException(_email)
  }

  /** Email setter. */
  def email_=(newEmail: Email): Unit =
    _email = update(StaticKey.email, newEmail)

  /** Picture getter. TODO: format to be determined. */
  def pic: Blob = _pic.getOrElse {
    _pic = redis(_.hget[Blob](Key.user, StaticKey.pic))
    valueOrDefault(_pic, "")
  }

  /** Picture setter. */
  def pic_=(newPic: String): Unit =
    _pic = update(StaticKey.pic, newPic)

  /** Creation datetime getter. */
  def created_at: OffsetDateTime = _created_at.getOrElse {
    _created_at = redis(_.hget[OffsetDateTime](Key.user, StaticKey.created_at))
    valueOrException(_created_at)
  }

  /** Update datetime getter. */
  def updated_at: OffsetDateTime = _updated_at.getOrElse {
    _updated_at = redis(_.hget[OffsetDateTime](Key.user, StaticKey.updated_at))
    valueOrException(_updated_at)
  }

  /** Connected datetime getter. */
  def connected_at: OffsetDateTime = _connected_at.getOrElse {
    _connected_at = redis(_.hget[OffsetDateTime](Key.user, StaticKey.connected_at))
    valueOrException(_connected_at)
  }

  /** Updates connection datetime. */
  def connected(): Unit =
    _connected_at = update(StaticKey.connected_at, Temporal.now)

  /** Groups getter. */
  def groups: List[NID] = _groups.getOrElse {
    _groups = redis(_.hkeys[NID](Key.groups))
    valueOrDefault(_groups, List.empty)
  }

  /** Groups with updates getter. */
  def groupsWithUpdates: List[(NID, OffsetDateTime, OffsetDateTime)] = {
    val currentGroup = groups // need to be computed before pipeline
    val query = redisPipeline { p =>
      currentGroup.map { nid =>
        val nodeKey = Group(nid).NodeKey.node
        p.hmget[String, OffsetDateTime](nodeKey, StaticNodeKey.updated_at, StaticNodeKey.refreshed_at)
      }
    }
    val res = valueOrException(query).asInstanceOf[List[Option[Map[String, OffsetDateTime]]]]
    currentGroup.zip(res.flatten).map { case (nid, values) =>
      (nid, values(StaticNodeKey.updated_at), values(StaticNodeKey.refreshed_at))
    }
  }

  /** Adds a group and returns whether this group has been added. */
  def addGroup(newGroup: NID): Boolean =
    addWithTime(Key.groups, newGroup)

  /** Remove a group and returns whether this group has been removed. */
  def removeGroups(oldGroups: NID): Boolean =
    remWithTime(Key.groups, oldGroups)

  /** Entourage getter. */
  def entourage: List[UID] = _entourage.getOrElse {
    _entourage = redis(_.hkeys[UID](Key.entourage))
    valueOrException(_entourage)
  }

  /** Entourage with updates getter. */
  def entourageWithUpdates: List[(UID, OffsetDateTime)] = {
    val currentEntourage = entourage // need to be computed before pipeline
    val query = redisPipeline { p =>
        currentEntourage.map { id =>
        val userKey = User(id).Key.user
        p.hget[OffsetDateTime](userKey, StaticKey.updated_at)
      }
    }
    val res = valueOrException(query).asInstanceOf[List[Option[OffsetDateTime]]].flatten
    assert(res.size == currentEntourage.size)
    currentEntourage.zip(res)
  }

  /** Adds a user and returns whether this user has been added. */
  def addEntourage(newUser: UID): Boolean =
    addWithTime(Key.entourage, newUser)

  /** Add multiple users. */
  def addEntourage(newUsers: List[UID]): Boolean =
    addWithTime(Key.entourage, newUsers)

  /** Remove a user and returns whether this user has been removed. */
  def removeEntourage(oldUser: UID): Boolean =
    remWithTime(Key.entourage, oldUser)

  /** Add multiple users. */
  def removeEntourage(oldUsers: List[UID]): Boolean =
    remWithTime(Key.entourage, oldUsers)

    /**
    * Loads the entire model for intensive usage (except entourage and groups).
    * Makes the hypothesis that the values are valid, if this is not the case, a second attempt to get them will be done
    * on the concerned getter call and issue either the default value or a corresponding exception.
    */
  def hydrate(): Unit = {
    val values = redis(_.hgetall[String, String](Key.user))
      .getOrElse(throw new IllegalValueException("node should have some data"))
    _name = values.get(StaticKey.name)
    _email = values.get(StaticKey.email)
    _pic = values.get(StaticKey.pic)
    _created_at = values.get(StaticKey.created_at).map(OffsetDateTime.parse)
    _updated_at = values.get(StaticKey.updated_at).map(OffsetDateTime.parse)
  }

}

/** [[User]] companion. */
object User {

  object StaticKey {
    val uid = "users:uid"
    val emailIndex = "users:indexes:email"
    val name = "name"
    val email = "email"
    val pic = "pic"
    val created_at = "created_at"
    val updated_at = "updated_at"
    val connected_at = "connected_at"
  }

  /**
    * Creates an user with an email and returns its new corresponding user.
    * User id incrementation is done at each creation.
    *
    * @param email user email
    * @return user
    */
  def create(email: String): User = {
    if (!redis(_.hexists(StaticKey.emailIndex, email))) {
      val uid = newIdentity()
      val user = User(uid)
      redis { r =>
        val now = Temporal.now
        val infos = List(
          (StaticKey.email, email),
          (StaticKey.created_at, now),
          (StaticKey.updated_at, now)
        )
        r.hmset(user.Key.user, infos)
        r.hset(StaticKey.emailIndex, email, uid)
      }
      user._email = Some(email)
      user
    } else throw new NewUserExistException("email already registered")
  }

  /** Retrieves user model given an user email. */
  def fromEmail(email: String): Option[User] = {

    import yields.server.actions._

    if (validEmail(email)) {
      redis(_.hget[UID](StaticKey.emailIndex, email)).map(User(_))
    } else throw new UnauthorizedActionException(s"invalid email in fromEmail method")
  }

  /** Prepares user model for retrieving data given an user id. */
  def apply(uid: UID): User = {
    new User(uid)
  }

}