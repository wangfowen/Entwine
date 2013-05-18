package smtp

import akka.actor.Actor
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import play.api.Logger

class SmtpScheduler(senderCount: Int) extends Actor {
  val smtpRouter = context.actorOf(Props[SmtpSender].withRouter(SmallestMailboxRouter(senderCount)), name = "smtpRouter")

  def receive = {
    case NewEmail(user) =>
      Logger.debug("SMTP: Attempting to send a test email")
      smtpRouter ! NewEmail(user)
  }
}