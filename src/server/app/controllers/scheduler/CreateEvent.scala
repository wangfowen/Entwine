package controllers.scheduler

import play.api.mvc._
import play.api.data._
import play.api.data.format.Formats._
import play.api.data.Forms._
import java.text.SimpleDateFormat
import java.util.Date
import play.api.i18n.Messages

object CreateEvent extends Controller {

  val createEventForm = Form(
    tuple (
      "name" -> text,
      "location" -> optional(text),
      "description" -> optional(text),
      "cutoff" -> of[Long],
      "invitees" -> text
    )
  )

  def shortDateFormat = new SimpleDateFormat("MM/dd/yyyy")

  def index() = Action { implicit request =>
    Ok(views.html.scheduler.createEvent())
  }

  def createEvent() = Action { implicit request =>
    Ok(views.html.scheduler.createEvent())
  }

}
