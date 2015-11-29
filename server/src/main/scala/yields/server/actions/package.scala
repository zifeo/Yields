package yields.server

package object actions {

  private lazy val mailPattern = """\S+@\S+\.\S+""".r
  private lazy val urlPattern = """https?://\S+.\S+?""".r

  /**
    * Check whether an email is valid.
    * @param mail email to test
    * @return true if valid
    */
  def validEmail(mail: String): Boolean =
    mailPattern.unapplySeq(mail).nonEmpty

  /**
    * Check whether an url is valid
    * @param url url to test
    * @return true if valid
    */
  def validURL(url: String): Boolean =
    urlPattern.unapplySeq(url).nonEmpty

}
