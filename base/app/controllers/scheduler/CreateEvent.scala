package controllers.scheduler

import controllers.Authentication
import application.Global
import play.api.mvc.Controller
import play.api.data._
import play.api.data.format.Formats._
import play.api.data.Forms._
import database.api._
import database.models._
import services.api._
import services.actors._
import services.actors.AutomailerActor
import java.text.SimpleDateFormat
import java.util.Date
import com.codahale.jerkson.Json
import play.api.i18n.Messages

object CreateEvent extends Controller with Authentication {
	
	val createEventForm = Form(
		tuple (
			"name" -> text,
			"location" -> optional(text),
			"description" -> optional(text),
			"cutoff" -> of[Long],
			"invitees" -> text
		)
	)

	def shortDateFormat = new SimpleDateFormat("MM/dd/yyyy")

	def index = isAuthenticatedUser { account: FullAccount => implicit request =>
		Ok(views.html.scheduler.createEvent(account))
	}

	def createEvent = isAuthenticatedUser { account: FullAccount => implicit request =>
		val formData = createEventForm.bindFromRequest
		if(formData.hasErrors)
			throw new IllegalArgumentException("Create event form error: " + formData.errors)
		var (name, location, description, cutoff, inviteeStr) = formData.get

		var event = EventsAPI.bindEventData(
			Map(
				"name" -> name,
				"location" -> location.getOrElse(null),
				"description" -> description.getOrElse(null),
				"cutoffDate" -> new Date(cutoff)
			)
		)
		
		event.addParticipant(account.getEmailAccount())
		event.setOwner(account)
		event.setStatus(Event.Status.NEW)

		EventsAPI.save(event)
		println(controllers.scheduler.routes.SelectTime.index(event.getId(), account.getId()).absoluteURL())
		Global.automailerActor.tell(EmailMsg(account.getEmailAccount().getEmail(),
				Messages("scheduler.email.onCreateEvent.creator.subject",
						event.getName()),
				Messages("scheduler.email.onCreateEvent.creator.body",
						controllers.scheduler.routes.SelectTime.index(event.getId(), account.getId()).absoluteURL())
				))
				
		if(inviteeStr != null){
			var inviteeList = Json.parse[List[String]](inviteeStr)
			inviteeList.foreach { emailStr =>
				var part = AccountsAPI.getEmailAccountByEmail(emailStr, true)
				Global.automailerActor.tell(EmailMsg(part.getEmail(),
						Messages("scheduler.email.onCreateEvent.invitee.subject",
								account.getFormattedName(),
								event.getName()),
						Messages("scheduler.email.onCreateEvent.invitee.body",
								controllers.scheduler.routes.SelectTime.index(event.getId(), part.getId()).absoluteURL())
						))
				event.addParticipant(part)
			}
		}

		EventsAPI.save(event)
		Redirect(controllers.scheduler.routes.SelectTime.index(event.getId()))
	}
	
}