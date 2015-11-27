![](./yields.jpg)

- - -

### Yet another internet messaging app with really simple syndication aggregator.

[![Build Status](https://jenkins.epfl.ch/buildStatus/icon?job=2015-team-rocket)](https://jenkins.epfl.ch/job/2015-team-rocket/)

## Protocol

### Comprehension Hierarchy 

```
Node
 |--active
 |     |--Group (private)
 |     |--Publish
 |     |--RSS
 |
 |--passive
       |--Media
```

### Types

```
UID: Long
NID: Long
Action
Result
```

Suffix actions with `Res` for corresponding results.

### Messages

Request, response, error, notification (bcast).

```
Messages
	input	kind: String, message: Action, metadata: Metadata
	output	(1) kind: String, message: Result, metadata: Metadata
	output	(2) message: String, metadata: Metadata
	rules	success (1) | error (2)
Metadata
	input	client: UID, datetime: OffsetDateTime, ref: OffsetDateTime
	output	client: UID, datetime: OffsetDateTime, ref: OffsetDateTime
	rules	client & ref unchanged
```

### Users actions

```
UserConnect
	input 	mail: String
	output	uid: UID, new: Boolean
UserUpdate
	input 	mail: Option[String], name: Option[String], pic: Option[Array[Byte]], addEntourage: Seq[UID], removeEntourage[UID]
	output	()
	bcast	uid: UID, name: String, pic: Array[Byte]
	notice	no mail for now
UserInfo
	input	uid: UID
	output	(1) uid: UID, mail: String, name: String, pic: Array[Byte], entourage: Seq[UID], entourageUpdatedAt: Seq[OffsetDateTime]
	output	(2) uid: UID, mail: String, name: String, pic: Array[Byte], entourage: Seq.empty, entourageUpdatedAt: Seq.empty
	rules	uid == m.uid (1) | uid in entourage (2)
UserGroupList
	input	()
	output	groups: Seq[NID], names: Seq[String], updatedAt: Seq[OffsetDateTime], refreshedAt: Seq[OffsetDateTime]
UserSearch
	input	mail: String
	output	uid: UID, name: String, pic: Array[Blob]
```

### Nodes actions

```
NodeHistory
	input	nid: NID, datetime: OffsetDateTime, count: Int
	output	nid: NID, datetimes: Seq[OffsetDateTime], senders: Seq[UID], texts: Seq[Option[String]], contentTypes: Seq[Option[String]], contents: Seq[Option[Array[Byte]]
	rules	count > 0 & nid in nodes & senders in entourage
NodeSearch
	input	pattern: String
	output	nodes: Seq[NID], names: Seq[String], pic: Seq[Array[Blob]]
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
	output	nid: NID, name: String, pic: Option[Array[Byte]], users: Seq[UID], nodes: Seq[NID]
	rules	nid in nodes
GroupMessage
	input	nid: NID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]]
	output	nid: NID, datetime: OffsetDateTime
	rules	nid in nodes
	bcast	nid: NID, datetime: OffsetDateTime, sender: UID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]]
```

### Publisher actions

```
PublisherCreate
	input	name: String, users: Seq[UID], nodes: Seq[NID]
	output	nid: NID
	rules	users in entourage & (nodes "public" | nodes in groups)
	bcast	nid: NID, name: String, users: Seq[UID], nodes: Seq[NID]
PublisherUpdate
	input nid: NID, name: Option[String], pic: Option[Array[Byte]], addUsers: Seq[UID], removeUsers: Seq[UID], addNodes: Seq[NID], removeNodes: Seq[NID]
	output	()
	rules	nid in groups
	bcast	nid: NID, name: String, pic: Array[Byte], users: Seq[UID], nodes: Seq[NID]
PublisherInfo
	input	nid: NID
	output	nid: NID, name: String, pic: Option[Array[Byte]], users: Seq[UID], nodes: Seq[NID]
	rules	nid in nodes
PublisherMessage
	input	nid: NID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]]
	output	nid: NID, datetime: OffsetDateTime
	rules	nid in nodes
	bcast	nid: NID, datetime: OffsetDateTime, sender: UID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]]
```

### RSS actions

```
RSSCreate
	input	name: String, url: String
	output	nid: NID
	bcast	nid: NID, name: String, url: String
RSSMessage
	bcast	nid: NID, datetime: OffsetDateTime, sender: UID, text: Option[String], contentType: Option[String], content: Option[Array[Byte]]
```
