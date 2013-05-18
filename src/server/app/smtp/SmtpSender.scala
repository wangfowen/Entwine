package smtp

import akka.actor.Actor
import org.apache.commons.mail.{HtmlEmail, SimpleEmail}

class SmtpSender extends Actor {
  def receive = {
    case NewEmail(user) =>
      sendNewEmail(user)
  }

  def sendNewEmail(user: Int) {
    val subject = "A new email from %d".format(user)
    val textBody = "A new test email from %d".format(user)
    val htmlBody = "A new test email from %d".format(user)

    sendHtmlEmail("supernuber@gmail.com", subject, textBody, htmlBody)
  }

  private def sendSimpleEmail(to: String, subject: String, body: String) {
    val email = new SimpleEmail()
    email.setHostName("localhost")
    email.setSmtpPort(25)
    email.setFrom("noreply@entwine.us", "Entwine")
    email.addTo(to)
    email.setSubject(subject)
    email.setMsg(body)
    email.send()
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
