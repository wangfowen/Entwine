package controllers.scheduler.rest

import java.util.Calendar
import java.util.Date
import org.apache.commons.lang3.time.DateUtils
import com.codahale.jerkson.Json
import database.api._
import database.models.TimeBlock
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.Controller
import application.Global
import services.actors._
import play.api.i18n.Messages

case class TimeBlockCC(id: Long, start: Long, end: Long, allDay: Boolean)
	
object ParticipationsRest extends Controller {
	
	private def getOtherParticipations(_eid: Long, _uid: Long): Set[Any] = {
		ParticipationsAPI.getParticipationByEventId(_eid, _uid).map(p => p.toJSONMap())
	}
	
	def get = Action { implicit request =>
		val getForm = Form(
			tuple(
				"userId" -> optional(longNumber),
				"eventId" -> longNumber,
				"getOtherTimes" -> optional(boolean)
			)
		)
		
		getForm.bindFromRequest.fold(
			formWithErrors => {
				Ok(Json.generate({"error" -> formWithErrors.toString()}))
			},
			{ case (_userId, _eventId, _getOtherTimes) =>
				var eid = _eventId
				var uid:Long = _userId.getOrElse(-1)
				var participation = ParticipationsAPI.getParticipationById(eid, uid)
				var retmap = Map(
					"otherPart" -> Set(),
					"currentPart" -> participation.toJSONMap()
				)
			
				if (_getOtherTimes.getOrElse(false))
					retmap += ("otherPart" -> getOtherParticipations(eid, uid))
					
				Ok(Json.generate(retmap))
			}
		)
	}

	def post = Action { implicit request =>
		
		val TBCC = mapping (
			"id" -> longNumber,
			"start" -> longNumber,
			"end" -> longNumber,
			"allDay" -> boolean
		)(TimeBlockCC.apply)(TimeBlockCC.unapply)
		
		val postForm = Form(
			tuple(
				"userId" -> number,
				"eventId" -> number,
				"getOtherTimes" -> optional(boolean),
				"timeBlocks" -> list(TBCC),
				"deletedTimeBlocks" -> list(longNumber)
			)
		)
		
		postForm.bindFromRequest.fold(
			formWithErrors => {
				println(formWithErrors)
				Ok(Json.generate({"error" -> formWithErrors.toString()}))
			},
			{
				case (_userId, _eventId, _getOtherTimes, _timeBlocks, _deletedTimeBlocks) => {
					var eid = _eventId;
					var uid = _userId;
					
					var participation = ParticipationsAPI.getParticipationById(eid, uid)
					var hasAccepted = (participation.getTimeBlocks().size() != 0)
					var event = participation.getEvent();
					var timeBlockArray = _timeBlocks
					var deleteIdArray = _deletedTimeBlocks
					var participationJSON = participation.toJSONMap()
					var retmap = Map[String, Any](
						"otherPart" -> Set()
					)
					
					deleteIdArray.foreach { id =>
						participation.removeTimeBlock(id)
					}
					
					timeBlockArray.foreach { tbcc =>
						var timeblock = new TimeBlock()
						var id = tbcc.id
						var startDate = new Date(tbcc.start)
						var endDate = new Date(tbcc.end)
						
						if(tbcc.allDay){
							startDate = DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH)
							endDate = DateUtils.addDays(startDate, 1)
						}
						
						timeblock.setId(if (id == -1) null else id);
						timeblock.setStartTime(startDate)
						timeblock.setEndTime(endDate)
						participation.addTimeBlock(timeblock)
					}
					ParticipationsAPI.save(participation)
					
					Global.autoschedulerActor.tell(ParticipationMsg(participation))
					
					if (!hasAccepted && uid != event.getOwner().getId())
						Global.automailerActor.tell(EmailMsg(
								event.getOwner().getEmailAccount().getEmail(),
								Messages("scheduler.email.onAcceptInvitation.creator.subject", participation.getParticipant().getEmail()),
								Messages("scheduler.email.onAcceptInvitation.creator.body",
										participation.getParticipant().getEmail(),
										event.getName(),
										"link")))
			
					participationJSON.put("eventId", event.getId())
					participationJSON.put("eventId", event.getId())
					participationJSON.put("userId", participation.getParticipant().getId())
					participationJSON.put("email", participation.getParticipant().getEmail())
					
					retmap += ("currentPart" -> participation.toJSONMap())
					if (_getOtherTimes.getOrElse(false))
						retmap += ("otherPart" -> getOtherParticipations(eid, uid))
			
					Ok(Json.generate(retmap))
				}
			}
		)
	}
	
}