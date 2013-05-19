package controllers.account

import controllers.Authentication
import play.api.mvc.Controller

object Settings extends Controller with Authentication {
  def index = IsAuthenticated { userId => implicit request =>
    Ok(views.html.account.settings())
  }
}
