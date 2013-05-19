package controllers.scheduler

import play.api._
import play.api.data.Forms._
import play.api.mvc._
import controllers.Authentication

object Dashboard extends Controller with Authentication {

  def index () = IsAuthenticated { userId => request =>
    Ok(views.html.scheduler.dashboard())
  }

}
