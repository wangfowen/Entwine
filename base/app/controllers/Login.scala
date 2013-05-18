package controllers

import database.api.AccountsAPI
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Session
import views.html.login
import application.Global
import services.actors.EmailMsg

object Login extends Controller {
	val subscribeForm = Form (
		tuple (
			"email" -> text,
			"password" -> text
		)
	)
	
	private def redirect (session: Session): Result = {
		var url = session.get("loginRedirect").getOrElse("")
		
		if(url.length != 0)
			Redirect(url).withSession(session)
		else
			Redirect(scheduler.routes.Dashboard.index).withSession(session)
	}
	
	def index (email: String) = Action { implicit request =>
		var session = request.session
		if (session != null) {
			var id = session.get("userId")
			if (id.isDefined && AccountsAPI.getFullAccountById(id.getOrElse("-1").toLong) != null)
				this.redirect(session)
		}
		Ok(views.html.login(email))
	}
	
	def signup (_email: String, _password: String) = Action { implicit request =>
		var email = _email
		var password = _password
		
		try {
			val formdata = subscribeForm.bindFromRequest.get
			if (email == null)
				email = formdata._1
			if (password == null)
				password = formdata._2
				
			var account = AccountsAPI.createFullAccount(email, password)
			var session = request.session - "loginRedirect" + ("userId" -> account.getId().toString())

			Global.automailerActor.tell(EmailMsg(
					account.getEmailAccount().getEmail(),
					"",
					""))
			this.redirect(session)
		}
		catch {
			case e: Exception =>
				Ok(views.html.login(email, Some(e.getMessage())))
		}
	}
	
	def login (_email: String, _password: String) = Action { implicit request =>
		var email = _email
		var password = _password
		
		try {
			val formdata = subscribeForm.bindFromRequest.get
			if (email == null)
				email = formdata._1
			if (password == null)
				password = formdata._2
				
			var account = AccountsAPI.authenticate(email, password)
			var session = request.session - "loginRedirect" + ("userId" -> account.getId().toString())

			this.redirect(session)
		}
		catch {
			case e: Exception =>
				Ok(views.html.login(email, Some(e.getMessage())))
		}
	}
	
}