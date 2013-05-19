package controllers.scheduler

import play.api._
import play.api.data.Forms._
import play.api.mvc._
import scala.collection.JavaConversions
import controllers.Authentication

object Dashboard extends Controller with Authentication {

  def index () = isAuthenticated { userId => implicit request =>
    Ok(views.html.scheduler.dashboard())
  }

}
