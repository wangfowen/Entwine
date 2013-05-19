package smtp

import models.User

sealed trait SmtpMessage
case class NewInvite(link: String, user: User) extends SmtpMessage
case class GenericEmail(receipient: String, subject: String, body: String) extends SmtpMessage
