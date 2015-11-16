package yields.server

import java.util.regex.{Matcher, Pattern}

package object actions {

  /**
    * Check if an email is valid
    * @param email email to test
    * @return boolean
    */
  def checkValidEmail(email: String): Boolean = {
    lazy val validEmail = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}".r
    email match {
      case validEmail() => true
      case _ => false
    }
  }

}
