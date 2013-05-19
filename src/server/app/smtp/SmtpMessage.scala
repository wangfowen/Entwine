package smtp

import models.User

sealed trait SmtpMessage
case class NewInvite(email: String, link: String, user: User) extends SmtpMessage
