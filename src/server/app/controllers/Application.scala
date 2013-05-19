package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views.html.index
import akka.actor.{Props, ActorSystem}
import smtp.SmtpScheduler

object Application extends Controller {
  val subscribeForm = Form(("email" -> text))
  val EMAIL_MIN_LENGTH = 3
  val PASSWORD_MIN_LENGTH = 6
  val SMTP_SENDER_COUNT = 3

  private val smtpSystem = ActorSystem("SmtpSystem")
  val smtpScheduler = smtpSystem.actorOf(Props(new SmtpScheduler(SMTP_SENDER_COUNT)), name = "smtpScheduler")

  val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$""".r

  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText
    )
  )

  val registerForm = Form(
    tuple(
      "email" -> email.verifying("Email must be at least " + EMAIL_MIN_LENGTH + " characters long.", _.length >= EMAIL_MIN_LENGTH),
      "password" -> nonEmptyText.verifying("Password must be at least " + PASSWORD_MIN_LENGTH + " characters long.", _.length >= PASSWORD_MIN_LENGTH),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )
  )

  def index = Action {
    Ok(views.html.index())
  }

  def loginHandler = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors =>
        BadRequest,
      values =>
        User.authenticate(values._1, values._2) match {
          case Some(x) =>
            Redirect(routes.Application.index).withSession(
              session + ("userId" -> x.userId.toString) + ("connTime" -> (System.currentTimeMillis / 1000).toString))
          case None =>
            BadRequest
        }
    )
  }

  def registerHandler = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      formWithErrors =>
        BadRequest,
      values =>
        (User.register _).tupled(values) match {
          case Some(userId) =>
            Redirect(routes.Application.index).withSession("userId" -> userId.toString)
          case None =>
            BadRequest
        }
    )
  }
}
