package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import database.api.AccountsAPI
import application.Global
import services.actors.AutomailerActor
import services.actors.EmailMsg
import views.html.index

object Main extends Controller {
	
	val subscribeForm = Form(("email" -> text))
	
	def index() = Action {
		Ok(views.html.index())
	}
	
	def subscribe(_email: String) = Action { implicit request =>
		try {
			var email = _email
			if (email == null)
				email = subscribeForm.bindFromRequest.get
				
			try {	
				val subscriber = AccountsAPI.createSubscriber(email)
			
				Global.automailerActor.tell(
					EmailMsg(
						subscriber.getEmailAccount.getEmail(),
						"",
						""
					)
				)
				
				Redirect(controllers.routes.Login.index(email))
			}
			catch {
				case e: IllegalArgumentException =>
					BadRequest(views.html.index(Some(e.getMessage())))
			}
		}
	}
	
}