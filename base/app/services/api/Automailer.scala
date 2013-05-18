package services.api

import org.apache.commons.mail._
import play.api.Play

object Automailer {
	def sendEmail(to: String, subject: String, message: String): Unit = {
		var email = new HtmlEmail()
		email.setSmtpPort(587)
		email.setAuthenticator(new DefaultAuthenticator(
				Play.current.configuration.getString("mailer.default.email").getOrElse(null),
				Play.current.configuration.getString("mailer.default.password").getOrElse(null)))
		email.setDebug(false)
		email.setHostName("smtp.gmail.com")
		email.getMailSession().getProperties().put("mail.smtps.auth", "true")
		email.getMailSession().getProperties().put("mail.debug", "true")
		email.getMailSession().getProperties().put("mail.smtps.port", "587")
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.port", "587")
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.class",   "javax.net.ssl.SSLSocketFactory")
		email.getMailSession().getProperties().put("mail.smtps.socketFactory.fallback", "false")
		email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true")
		email.setFrom(
				Play.current.configuration.getString("mailer.default.alias").getOrElse(null),
				Play.current.configuration.getString("mailer.default.name").getOrElse(null))
		email.setSubject(subject)
		email.setHtmlMsg(message)
		email.addTo(to.trim(), to.trim())
		email.setTLS(true)
		email.send()
	}
}