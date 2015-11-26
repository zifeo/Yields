![](./yields.jpg)

- - -

### Yet another internet messaging app with really simple syndication aggregator.

[![Build Status](https://jenkins.epfl.ch/buildStatus/icon?job=2015-team-rocket)](https://jenkins.epfl.ch/job/2015-team-rocket/)

## Protocol

### Users

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
	output	uid: UID, mail: String, name: String, pic: Array[Byte], entourage: Seq[UID], entourageUpdatedAt: Seq[OffsetDateTime]
	rule	uid == m.uid | uid in entourage (no entourage & entourageUpdatedAt)
UserGroupList
	input	()
	output	groups: Seq[NID], names: Seq[String], updatedAt: Seq[OffsetDateTime], refreshedAt: Seq[OffsetDateTime]
UserSearch
	input	mail: String
	output	uid: UID, name: String, pic: Array[Blob]
```

