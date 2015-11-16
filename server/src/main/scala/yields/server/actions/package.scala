package yields.server

import java.util.regex.{Matcher, Pattern}

package object actions {

  lazy val p: Pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")

  /**
    * Check if an email is valid
    * @param email email to test
    * @return boolean
    */
  def checkValidEmail(email: String): Boolean = {
    val m: Matcher = p.matcher(email)
    m.find
  }

}
