package controllers.scheduler.rest

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import database.api._
import com.codahale.jerkson._
import services.api.Autoscheduler

object EventsRest extends Controller {
	def get = Action { implicit request =>
		val getForm = Form(
			tuple (
				"userId" -> optional(number),
				"eventId" -> optional(number)
			)
		)
		var formData = getForm.bindFromRequest
		var eventData = formData.get
		var event = EventsAPI.getEventById(eventData._2.getOrElse(-1).asInstanceOf[Int])
		var timeBlock = Autoscheduler.findCommonTime(event)
		Ok(Json.generate(timeBlock.head.toJSONMap()))
	}
}