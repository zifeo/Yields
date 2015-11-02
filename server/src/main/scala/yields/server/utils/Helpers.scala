package yields.server.utils

import java.time.OffsetDateTime
import java.util.Date

object Helpers {

  /** Returns current date and time. */
  def currentDatetime: OffsetDateTime = Converters.date2OffsetDateTime(new Date())

}
