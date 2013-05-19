package smtp

import akka.actor.Actor
import org.apache.commons.mail.{HtmlEmail, SimpleEmail}
import models.User

class SmtpSender extends Actor {
  def receive = {
    case NewInvite(email, link, user) =>
      sendNewInvite(email, link, user)
    case GenericEmail(receipient, subject, body) =>
      sendHtmlEmail(receipient, subject, body, body)
  }

  def sendNewInvite(email: String, link: String, user: User) {
    println(user.firstName.get + " " + user.lastName.get)
    val subject = "%s has invited you an event!".format(user.firstName.get + " " + user.lastName.get)
    val textBody = "%s has invited you to an event. Please visit the following link to choose your availability. %s".format(user.firstName + " " + user.lastName, link)
    val htmlBody = "%s has invited you to an event! Please choose your availability <a href=\"%s\">here</a>".format(user.firstName + " " + user.lastName, link)

    sendHtmlEmail(email, subject, textBody, htmlBody)
  }

  private def sendHtmlEmail(to: String, subject: String, textBody: String, htmlBody: String) {
    val email = new HtmlEmail()
    email.setHostName("localhost")
    email.setSmtpPort(25)
    email.setFrom("noreply@entwine.us", "Entwine")
    email.addTo(to)
    email.setSubject(subject)
    email.setTextMsg(textBody)
    email.setHtmlMsg(htmlBody)
    email.send()
  }
}
