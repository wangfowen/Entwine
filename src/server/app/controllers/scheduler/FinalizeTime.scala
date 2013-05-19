package controllers.scheduler

import controllers.Authentication
import java.text.SimpleDateFormat
import play.api.data.Form
import play.api.mvc.Controller
import play.mvc.Action

object FinalizeTime extends Controller with Authentication {
  def index (eventId: Long) = isAuthenticated { userId => implicit request =>
    Ok(views.html.scheduler.finalizeTime(eventId))
  }
}