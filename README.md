![](./yields.jpg)

- - -

### Yet another internet messaging app with really simple syndication aggregator.

[![Build Status](https://jenkins.epfl.ch/buildStatus/icon?job=2015-team-rocket)](https://jenkins.epfl.ch/job/2015-team-rocket/)

## Protocol

### Messages

```
Request
	input	kind: String, message: Action, metadata: Metadata
	output	(1) kind: String, message: Result, metadata: Metadata
	output	(2) message: String, metadata: Metadata
	rule	success (1) | error (2)
Metadata
	input	client: UID, datetime: OffsetDateTime, ref: OffsetDateTime
	output	client: UID, datetime: OffsetDateTime, ref: OffsetDateTime
	rules	client & ref unchanged
```

Suffix actions with `Res` for results.

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

