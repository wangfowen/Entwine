package controllers.account.rest

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import database.api._
import com.codahale.jerkson._
import services.api.Autoscheduler
import controllers.Authentication

object AccountRest extends Controller {
	object Password extends Controller with Authentication {
		def put = isAuthenticatedUser { account => implicit request =>
			val putForm = Form(
				tuple(
					"oldPassword" -> text,
					"newPassword" -> text
				)
			)
			var formData = putForm.bindFromRequest
			var passwords = formData.get
			var ret = Map[String, Any]()
			var error: Option[String] = None
			
			if (account.comparePassword(passwords._1)) {
				try
					account.changePassword(passwords._2)
				catch {
					case e: Exception =>
						error = Some(e.getMessage())
				}
			}
			else
				error = Some("The old password you provided is incorrect.")
			
			if (!error.isEmpty) {
				ret += ("error" -> error.get)
				Ok(Json.generate(ret))
			}
			else
				Ok(Json.generate(ret))
		}
	}
	
	object Email extends Controller with Authentication {
		def put = isAuthenticatedUser { account => implicit request =>
			var putForm = Form(
				("newEmail" -> text)
			)
			var formData = putForm.bindFromRequest
			var email = formData.get
			var ret = Map[String, Any]()
			var error: Option[String] = None
			
			try {
				account.changeEmail(email)
				ret += ("email" -> email)
			}
			catch {
				case e: Exception =>
					error = Some(e.getMessage())
			}
			
			if (error.isDefined) {
				ret += ("error" -> error.get)
				Ok(Json.generate(ret))
			}
			else
				Ok(Json.generate(ret))
		}
	}
}