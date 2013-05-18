package services.actors

import akka.actor.Actor
import services.api._
import database.api._
import database.models._
import application.Global
import scala.collection.mutable.StringBuilder
import play.api.i18n.Messages
import util.control.Breaks._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions

case class ParticipationMsg (participation: Participation)

class AutoschedulerActor extends Actor {
	
	def receive = {
		case ParticipationMsg(participation) => {
			var event = participation.getEvent()
			var numIn = event.getParticipationCount()
			var numTotal = event.getInvitedCount()
			
			if (numIn/numTotal >= Global.MIN_PARTICIPATION_RATIO) {
				var timeslots = Autoscheduler.findCommonTime(event)
				var emailslots = ListBuffer[CommonTimeBlock]()
				
				EventsAPI.removeCommonTimeBlocks(event)
				
				timeslots.foreach(timeslot => {
					if (timeslot.getParticipants().size() == numIn) {
						var common = new CommonTimeBlock(timeslot)
						event.addCommonTimeBlock(common)
						emailslots.append(common)
						EventsAPI.save(event)
					}
				})
				
				
				if (emailslots.size > 0) {
					var sb1 = new StringBuilder()
					var sb2 = new StringBuilder()
					var selected = emailslots(0)
					var participantList = JavaConversions.asScalaBuffer(event.getParticipationList())
					var counter = 0
					
					breakable {
						emailslots.foreach(timeslot => {
							if (counter >= Global.NUM_DISPLAY_COMMON_TIME)
								break
							
							sb2.append("    " + Messages("scheduler.timeRangeString",
									Global.DATE_FORMATTER.format(timeslot.getStartTime()),
									Global.DATE_FORMATTER.format(timeslot.getEndTime()))
									+ "<br/>")
							counter += 1
						})
					}
						
					Global.automailerActor.tell(EmailMsg(
							event.getOwner().getEmailAccount().getEmail(),
							Messages("scheduler.email.onScheduleEvent.creator.subject", event.getName()),
							Messages("scheduler.email.onScheduleEvent.creator.body",
									event.getOwner().getFormattedName(),
									event.getName(),
									"<br/>    " + Messages("scheduler.timeRangeString",
											Global.DATE_FORMATTER.format(selected.getStartTime()),
											Global.DATE_FORMATTER.format(selected.getEndTime())),
									sb2.toString(),
									"link")))
					
									
					participantList.foreach(participation => {
						if (participation.getParticipant().getId() != event.getOwner().getId()) {
							Global.automailerActor.tell(EmailMsg(
									participation.getParticipant().getEmail(),
									Messages("scheduler.email.onScheduleEvent.invitee.subject", event.getName()),
									Messages("scheduler.email.onScheduleEvent.invitee.body",
											participation.getParticipant().getEmail(),
											event.getName(),
											Messages("scheduler.timeRangeString",
													Global.DATE_FORMATTER.format(selected.getStartTime()),
													Global.DATE_FORMATTER.format(selected.getEndTime())))))
						}
					})
				}
				
				println("Found common timeslots: " + timeslots)
			}
			else
				println("Not enough to cross threshold")
		}
		case _ =>
	}
	
}