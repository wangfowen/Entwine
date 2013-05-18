package controllers

import play.api._
import play.api.mvc._
import akka.actor.{Props, ActorSystem}
import smtp.SmtpScheduler

object Application extends Controller {
  val EMAIL_MIN_LENGTH = 3
  val PASSWORD_MIN_LENGTH = 6
  val SMTP_SENDER_COUNT = 3

  private val smtpSystem = ActorSystem("SmtpSystem")
  val smtpScheduler = smtpSystem.actorOf(Props(new SmtpScheduler(SMTP_SENDER_COUNT)), name = "smtpScheduler")

  val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$""".r


  def index = Action {
    Ok(views.html.index())
  }

  def subscribe(_email: String) = Action {
    Ok(views.html.index())
  }

}
