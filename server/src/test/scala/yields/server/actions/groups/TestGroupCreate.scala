package yields.server.actions.groups

import org.scalacheck.{Test, Prop, Properties}
import yields.server.actions.ActionsGenerators
import yields.server.dbi.models.{Group, NID, UID}
import yields.server.mpi.Metadata
import yields.server.utils.Temporal

/**
  * Test if GroupCreate action performed well
  */
object TestGroupCreate extends Properties("GroupCreate") with ActionsGenerators {

  import Prop.forAll

  lazy val m = new Metadata(1, Temporal.current)

  val propArgs = forAll { (a: GroupCreate) =>
    !a.name.isEmpty && !a.users.isEmpty
  }

  val propCreateGroup = forAll { (a: GroupCreate) =>
    val res = a.run(m)

    res match {
      case GroupCreateRes(nid) =>
        val group = Group(nid)
        group.name == a.name && group.users == a.users && group.nodes == a.nodes
      case _ => false
    }
  }

}
