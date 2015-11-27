package yields.server

package object actions {

  private lazy val mailPattern = "(?=[^\\s]+)(?=(\\w+)@([\\w\\.]+))".r

  /**
    * Check whether an email is valid.
    * @param mail email to test
    * @return true if valid
    */
  def validEmail(mail: String): Boolean =
    mailPattern.unapplySeq(mail).nonEmpty

}
