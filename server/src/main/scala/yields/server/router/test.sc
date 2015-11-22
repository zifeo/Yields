import akka.util.ByteString

val str = """{"kind":"GroupMessage","message":{"nid":2,"content":"hello"},"metadata":{"client":1, "datetime": "2007-12-03T10:15:30+01:00"}}"""

val FindUID = """.+"metadata".+"client":\s?([0-9]+).+""".r

str match {
  case FindUID(uid) => true
  case _ => false
}

ByteString(str).utf8String match {
  case FindUID(uid) => true
  case _ => false
}