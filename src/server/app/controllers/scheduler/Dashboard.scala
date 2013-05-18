package controllers.scheduler

import play.api._
import play.api.data.Forms._
import play.api.mvc._
import scala.collection.JavaConversions

object Dashboard extends Controller {

  def index () = Action { implicit request =>
    Ok(views.html.scheduler.dashboard())
  }

}
