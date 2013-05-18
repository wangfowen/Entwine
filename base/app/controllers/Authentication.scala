package controllers

import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Results
import play.api.mvc.Result
import play.api.mvc.Security
import play.api.mvc.Action
import play.api.mvc.AnyContent
import database.api.AccountsAPI
import database.models._

trait Authentication {
	
	private var allowAnon = false;
	
	def setAllowAnon (aa: Boolean) = this.allowAnon = aa
	def isAllowAnon (): Boolean = this.allowAnon
	
	/**
	 * Retrieves the connected user id
	 */
	private def getUserId (request: RequestHeader) = {
		var ret = request.session.get("userId")
		if (ret == null && allowAnon)
			ret = Option("-1")
		ret
	}

	/**
	 * Redirect to login if the user in not authorized
	 */
	private def onUnauthorized (request: RequestHeader) = {
		Results.Redirect(controllers.routes.Login.index()).withSession(
			request.session + ("loginRedirect" -> request.uri)
		)
	}
	
	/** 
	 * Checks if the user id is null or not
	 */
	def isAuthenticated (func: Long => Request[AnyContent] => Result) = {
		Security.Authenticated(getUserId, onUnauthorized){ userIdStr =>
			Action(request => func( if (userIdStr == null) -1 else userIdStr.toLong)(request) )
		}
	}
	
	
	/**
	 * Checks if the user id is a valid existing account, and returns that account
	 */
	def isAuthenticatedUser (func: FullAccount => Request[AnyContent] => Result) = {
		isAuthenticated { userId: Long => implicit request =>
			try {
				if (userId == -1) {
					if (this.isAllowAnon())
						func(null)(request)
					else {
						println("Error: how come a userId of -1 is in this block of code?")
						onUnauthorized(request)
					}
				}
				else {
					var account = AccountsAPI.getFullAccountById(userId)
					try {
						func(account)(request)
					}
					catch {
						case e: Exception =>
							println(e)
							onUnauthorized(request)
					}
				}
			}
			catch {
				case e: SecurityException =>
					println(e)
					onUnauthorized(request)
			}
		}
	}
	
}