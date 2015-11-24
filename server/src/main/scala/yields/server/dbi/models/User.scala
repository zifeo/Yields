package yields.server.dbi.models

import java.time.OffsetDateTime

import com.redis.serialization.Parse.Implicits._
import yields.server.actions.exceptions.{NewUserExistException, UnauthorizeActionException}
import yields.server.dbi._
import yields.server.dbi.exceptions.{IllegalValueException, UnincrementableIdentifierException}
import yields.server.utils.Temporal

/**
  * User model with linked database interface.
  *
  * Each attribute can be load separately on its own call to improve performance.
  * However work involving many of those, should manually call [[hydrate()]] to load all data.
  * Attributes new values are saved on respective setters.
  *
  * Database structure:
  * users:uid Long - last user id created
  * users:[uid] Map[String, String] - name / email / picture / created_at / updated_at / connected_at
  * users:[uid]:groups Zset[NID] with score datetime
  * users:[uid]:entourage Zset[UID] with score datetime
  * users:indexes:email Map[Email, UID] - email
  *
  * TODO : improve setters by only settings if the value is different.
  * TODO : what about a `save()` method? are there any use cases?
  * TODO : check for groups/users existance before adding/removing to groups/entourage
  * TODO : check if uid exists before trying to get an user with apply
  *
  * @param uid user id
  */
final class User private(val uid: UID) {

  object Key {
    val user = s"users:$uid"
    val name = "name"
    val email = "email"
    val picture = "picture"
    val created_at = "created_at"
    val updated_at = "updated_at"
    val connected_at = "connected_at"
    val groups = s"$user:groups"
    val entourage = s"$user:entourage"
  }

  private var _name: Option[String] = None
  private var _email: Option[Email] = None
  private var _picture: Option[Blob] = None
  private var _created_at: Option[OffsetDateTime] = None
  private var _updated_at: Option[OffsetDateTime] = None
  private var _connected_at: Option[OffsetDateTime] = None

  private var _groups: Option[List[NID]] = None
  private var _entourage: Option[List[UID]] = None

  /** Name getter. */
  def name: String = _name.getOrElse {
    _name = redis.withClient(_.hget[String](Key.user, Key.name))
    valueOrDefault(_name, "")
  }

  /** Name setter. */
  def name_=(newName: String): Unit =
    _name = update(Key.name, newName)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val updates = List((field, value), (Key.updated_at, Temporal.now))
    redis.withClient(_.hmset(Key.user, updates))
    Some(value)
  }

  /** Email getter. */
  def email: Email = _email.getOrElse {
    _email = redis.withClient(_.hget[Email](Key.user, Key.email))
    valueOrException(_email)
  }

  /** Email setter. */
  def email_=(newEmail: Email): Unit =
    _email = update(Key.email, newEmail)

  /** Picture getter. TODO: format to be determined. */
  def picture: Blob = _picture.getOrElse {
    _picture = redis.withClient(_.hget[Blob](Key.user, Key.picture))
    valueOrDefault(_picture, "")
  }

  /** Picture setter. */
  def picture_=(newPic: String): Unit =
    _picture = update(Key.picture, newPic)

  /** Creation datetime getter. */
  def created_at: OffsetDateTime = _created_at.getOrElse {
    _created_at = redis.withClient(_.hget[OffsetDateTime](Key.user, Key.created_at))
    valueOrException(_created_at)
  }

  /** Update datetime getter. */
  def updated_at: OffsetDateTime = _updated_at.getOrElse {
    _updated_at = redis.withClient(_.hget[OffsetDateTime](Key.user, Key.updated_at))
    valueOrException(_updated_at)
  }

  /** Connected datetime getter. */
  def connected_at: OffsetDateTime = _connected_at.getOrElse {
    _connected_at = redis.withClient(_.hget[OffsetDateTime](Key.user, Key.connected_at))
    valueOrException(_connected_at)
  }

  /** Connected datetime getter. */
  def connected_at_=(offsetDateTime: OffsetDateTime): Unit =
    _connected_at = update(Key.connected_at, offsetDateTime)

  /** Groups getter. */
  def groups: List[NID] = _groups.getOrElse {
    _groups = redis.withClient(_.zrange[NID](Key.groups, 0, -1))
    valueOrDefault(_groups, List.empty)
  }

  /** Adds a group and returns whether this group has been added. */
  def addToGroups(nid: NID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zadd(Key.groups, Temporal.now.toEpochSecond, nid)))

  /** Remove a group and returns whether this group has been removed. */
  def removeFromGroups(nid: NID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zrem(Key.groups, nid)))

  /** entourage getter. */
  def entourage: List[UID] = _entourage.getOrElse {
    _entourage = redis.withClient(_.zrange[UID](Key.entourage, 0, -1))
    valueOrException(_entourage)
  }

  /** Adds a user and returns whether this user has been added. */
  def addToEntourage(uid: UID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zadd(Key.entourage, Temporal.now.toEpochSecond, uid)))


  /** Remove a user and returns whether this user has been removed. */
  def removeFromEntourage(uid: UID): Boolean =
    hasChangeOneEntry(redis.withClient(_.zrem(Key.entourage, uid)))

  /**
    * Loads the entire model for intensive usage (except entourage and groups).
    * Makes the hypothesis that the values are valid, if this is not the case, a second attempt to get them will be done
    * on the concerned getter call and issue either the default value or a corresponding exception.
    */
  def hydrate(): Unit = {
    val values = redis.withClient(_.hgetall[String, String](Key.user))
      .getOrElse(throw new IllegalValueException("node should have some data"))
    _name = values.get(Key.name)
    _email = values.get(Key.email)
    _picture = values.get(Key.picture)
    _created_at = values.get(Key.created_at).map(OffsetDateTime.parse)
    _updated_at = values.get(Key.updated_at).map(OffsetDateTime.parse)
  }

}

/** [[User]] companion. */
object User {

  object StaticKey {
    val uid = "users:uid"
    val emailIndex = "users:indexes:email"
  }

  /**
    * Creates an user with an email and returns its new corresponding user.
    * User id incrementation is done at each creation.
    *
    * @param email user email
    * @return user
    */
  def create(email: String): User = {
    if (!redis.withClient(_.hexists(StaticKey.emailIndex, email))) {
      val uid = redis.withClient(_.incr(StaticKey.uid))
        .getOrElse(throw new UnincrementableIdentifierException("new user identifier (uid) fails"))
      val user = User(uid)
      redis.withClient { r =>
        import user.Key
        val infos = List((Key.created_at, Temporal.now), (Key.email, email))
        r.hmset(user.Key.user, infos)
        r.hset(StaticKey.emailIndex, email, uid)
      }
      user
    } else throw new NewUserExistException("email already registered")
  }

  /** Retrieves user model given an user email. */
  def fromEmail(email: String): Option[User] = {

    import yields.server.actions._

    if (checkValidEmail(email)) {
      redis.withClient(_.hget[UID](StaticKey.emailIndex, email)).map(User(_))
    } else throw new UnauthorizeActionException(s"invalid email in fromEmail method")
  }

  /** Prepares user model for retrieving data given an user id. */
  def apply(uid: UID): User = {
    new User(uid)
  }

}