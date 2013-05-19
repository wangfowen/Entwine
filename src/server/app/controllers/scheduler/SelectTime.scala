package controllers.scheduler

import java.text.SimpleDateFormat
import controllers.Authentication
import play.api.data.Forms.of
import play.api.data.Forms.optional
import play.api.data.Forms.tuple
import play.api.data.format.Formats.longFormat
import play.api.data.Form
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import models.Participation
import play.api.mvc.AsyncResult

object SelectTime extends Controller with Authentication {

  def shortDateFormat = new SimpleDateFormat("MM/dd/yyyy")

  val selectTimeGetForm = Form(
    tuple (
      "userId" -> optional(of[Long]),
      "eventId" -> optional(of[Long])
    )
  )

  def index (eventId: Long, userId: Long) = Action { implicit request =>
    AsyncResult {
      future {
        Participation.getParticipationId(userId, eventId)
      } map {
        case Some(pid) =>
          Ok(views.html.scheduler.selectTime(if (userId == -1) {
            request.session.get("userId").getOrElse("-1").toLong
          } else {
            userId
          }, eventId, pid))
        case _ =>
          BadRequest
      }
    }
  }

}
