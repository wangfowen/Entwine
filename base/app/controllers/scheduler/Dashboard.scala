package controllers.scheduler

import controllers.Authentication
import database.models._
import play.api._
import play.api.data.Forms._
import play.api.mvc._
import scala.collection.JavaConversions

object Dashboard extends Controller with Authentication {
	
	def index = isAuthenticatedUser { account: FullAccount => implicit request =>
		Ok(views.html.scheduler.dashboard(account, JavaConversions.asScalaBuffer(account.getCreatedEventList())))
	}
	
}