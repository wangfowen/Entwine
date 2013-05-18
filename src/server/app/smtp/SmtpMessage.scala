package smtp

sealed trait SmtpMessage
case class NewEmail(user: Int) extends SmtpMessage
