![](./yields.png)

- - -

### Yet another internet messaging app with really simple syndication aggregator.

[![Build Status](https://jenkins.epfl.ch/buildStatus/icon?job=2015-team-rocket)](https://jenkins.epfl.ch/job/2015-team-rocket/)

## Protocol

### Abstract hierarchy 

```
User

Node
 |——active
 |     |——Group (private)
 |     |——Publish
 |     |——RSS
 |
 |——passive
       |——Media
```

### Types

```
UID
NID
Action
Result
```

Suffix action names with `Res` for corresponding result names.
`UID` and `NID` are backed by the same identifier on serverside and represented by `Long`. Identifier `0` is consider as nothing.

### Messages

```
Message
	input	kind: String, message: Action, metadata: Metadata
	output	(1) kind: String, message: Result, metadata: Metadata
	output	(2) message: String, metadata: Metadata
	rules	success (1) | error (2)
	bcast	kind: String, message: Result, metadata: Metadata
Metadata
	input	client: UID, ref: OffsetDateTime, datetime: OffsetDateTime
	output	client: UID, ref: OffsetDateTime, datetime: OffsetDateTime
	rules	client & ref unchanged
```

A `Request` will answer with a `Response` or an `Error` on bad operations (such as unauthorized actions). Push notifications will sometimes be broadcasted.

### Users actions

```
UserConnect
	input 	email: String
	output	uid: UID, returning: Boolean
UserUpdate
	input 	email: Option[String], name: Option[String], pic: Option[Array[Byte]], addEntourage: Seq[UID], removeEntourage: Seq[UID]
	output	()
	bcast	uid: UID, email: String, name: String, pic: Array[Byte]
	notice	no email for now
UserInfo
	input	uid: UID
	output	(1) uid: UID, email: String, name: String, pic: Array[Byte], entourage: Seq[UID], entourageUpdatedAt: Seq[OffsetDateTime]
	output	(2) uid: UID, email: String, name: String, pic: Array[Byte], entourage: Seq.empty, entourageUpdatedAt: Seq.empty
	rules	uid == client (1) | uid in entourage (2)
UserNodeList
	input	()
	output	nodes: Seq[NID], kind: Seq[String], updatedAt: Seq[OffsetDateTime], refreshedAt: Seq[OffsetDateTime]
UserSearch
	input	email: String
	output	uid: UID
	rules	uid == 0 if not found
```

### Nodes actions

```
NodeHistory
	input	nid: NID, datetime: OffsetDateTime, count: Int
	output	nid: NID, datetimes: Seq[OffsetDateTime], senders: Seq[UID], texts: Seq[String], contentTypes: Seq[Option[String]], contents: Seq[Option[Array[Byte]], contentNids: List[Option[NID]]
	rules	count > 0 & nid in nodes & senders in entourage
NodeSearch
	input	pattern: String
	output	nodes: Seq[NID], names: Seq[String], pics: Seq[Array[Byte]]
	rules	nodes "public"
```

### Groups actions

```
GroupCreate
	input	name: String, users: Seq[UID], nodes: Seq[NID]
	output	nid: NID
	rules	users in entourage & (nodes "public" | nodes in groups)
	bcast	nid: NID, name: String, users: Seq[UID], nodes: Seq[NID]
GroupUpdate
	input nid: NID, name: Option[String], pic: Option[Array[Byte]], addUsers: Seq[UID], removeUsers: Seq[UID], addNodes: Seq[NID], removeNodes: Seq[NID]
	output	()
	rules	nid in groups
	bcast	nid: NID, name: String, pic: Array[Byte], users: Seq[UID], nodes: Seq[NID]
GroupInfo
	input	nid: NID
	output	nid: NID, name: String, pic: Array[Byte], users: Seq[UID], nodes: Seq[NID]
	rules	nid in nodes
GroupMessage
	input	nid: NID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]], contentNid: Option[NID]
	output	nid: NID, datetime: OffsetDateTime
	rules	nid in nodes
	bcast	nid: NID, datetime: OffsetDateTime, sender: UID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]], contentNid: Option[NID]
```

### Publisher actions

```
PublisherCreate
	input	name: String, users: Seq[UID], nodes: Seq[NID], tags: Seq[String]
	output	nid: NID
	rules	users in entourage & (nodes "public" | nodes in groups)
	bcast	nid: NID, name: String, users: Seq[UID], nodes: Seq[NID]
PublisherUpdate
	input nid: NID, name: Option[String], pic: Option[Array[Byte]], addUsers: Seq[UID], removeUsers: Seq[UID], addNodes: Seq[NID], removeNodes: Seq[NID], addTags: Seq[String], removeTags: Seq[String]
	output	()
	rules	uid in users
	bcast	nid: NID, name: String, pic: Array[Byte], users: Seq[UID], nodes: Seq[NID]
PublisherInfo
	input	nid: NID
	output	nid: NID, name: String, pic: Option[Array[Byte]], users: Seq[UID], nodes: Seq[NID], tags: Seq[String]
PublisherMessage
	input	nid: NID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]], contentNid: Option[NID]
	output	nid: NID, datetime: OffsetDateTime
	rules	nid in nodes
	bcast	nid: NID, datetime: OffsetDateTime, sender: UID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]], contentNid: Option[NID]
```

Publishers is very similar to groups and even share some of its structures but most the request are separated for allowing further differences to appears.

### RSS actions

```
RSSCreate
	input	name: String, url: String, tags: Seq[String]
	output	nid: NID
	bcast	nid: NID, name: String, url: String
RSSInfo
	input 	nid: NID
	output	name: String, url: String, tags: Seq[String]
```

### Media actions

```
MediaMessage
	input 	nid: NID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]]
	output 	nid: NID, datetime: OffsetDateTime
	bcast 	nid: NID, datetime: OffsetDateTime, sender: UID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]]
```
