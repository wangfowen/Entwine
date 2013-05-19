package controllers.scheduler

import play.api._
import play.api.data.Forms._
import play.api.mvc._
import controllers.Authentication
import scala.collection.JavaConversions
import models.Event

object Dashboard extends Controller with Authentication {

  def index () = IsAuthenticated { userId => request =>
    val (x, y) = Event.getAll(userId)
    Ok(views.html.scheduler.dashboard(x, y))
  }

}
