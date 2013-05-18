package controllers

import play.api._
import play.api.mvc._

object Login extends Controller {

  def index (email: String) = Action { implicit request =>
    Ok(views.html.login(email))
  }

}
