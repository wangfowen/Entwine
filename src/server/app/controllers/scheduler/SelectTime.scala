package controllers.scheduler

import java.text.SimpleDateFormat
import controllers.Authentication
import database.api.AccountsAPI
import play.api.data.Forms.of
import play.api.data.Forms.optional
import play.api.data.Forms.tuple
import play.api.data.format.Formats.longFormat
import play.api.data.Form
import play.api.mvc.Action
import play.api.mvc.Controller
import database.api.EventsAPI

object SelectTime extends Controller with Authentication {
	
	def shortDateFormat = new SimpleDateFormat("MM/dd/yyyy")
		
	val selectTimeGetForm = Form(
		tuple (
			"userId" -> optional(of[Long]),
			"eventId" -> optional(of[Long])
		)
	)

	def index (_eventId: Long, _userId: Long) = Action { implicit request =>
		var eventId = _eventId;
		var event = EventsAPI.getEventById(eventId);
		var userId: Long = _userId;
		var sessionUID = request.session.get("userId").getOrElse("-1")
		
		if (userId == -1) {
			userId = AccountsAPI.getFullAccountById(sessionUID.toLong).getEmailAccount().getId()
			if(userId == -1)
				throw new IllegalArgumentException("Why is the user ID null in the session?")
		}
		if (eventId == -1)
			throw new IllegalArgumentException("Why is the event ID null here?")
		if (event == null)
			throw new NullPointerException("Grr, someone entered in an invalid event id..")

		Ok(views.html.scheduler.selectTime(userId, event))
	}
	
}