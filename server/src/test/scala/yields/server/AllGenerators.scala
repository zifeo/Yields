package yields.server

import yields.server.actions.{UsersGenerators, GroupsGenerators, ActionsGenerators}
import yields.server.mpi.MessagesGenerators

trait AllGenerators
  extends DefaultsGenerators
  with MessagesGenerators
  with ActionsGenerators
  with GroupsGenerators
  with UsersGenerators