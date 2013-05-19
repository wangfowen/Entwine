package controllers.scheduler

import controllers.Authentication
import play.api.mvc.Controller

object FinalizeTime extends Controller with Authentication {
  def index (eventId: Long) = IsAuthenticated { userId => request =>
    Ok(views.html.scheduler.finalizeTime(eventId))
  }
}