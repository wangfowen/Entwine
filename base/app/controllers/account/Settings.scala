package controllers.account
import controllers.Authentication
import database.models.FullAccount
import play.api.mvc.Controller

object Settings extends Controller with Authentication {
	def index = isAuthenticatedUser { account: FullAccount => implicit request =>
		Ok(views.html.account.settings(account))
	}
}