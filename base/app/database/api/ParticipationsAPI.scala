package database.api

import java.util.Date
import database.models._
import com.avaje.ebean.Ebean
import scala.collection.JavaConversions

object ParticipationsAPI {
	
	def getParticipationById(eventId: Long, userId: Long): Participation = {
		Ebean.find(classOf[Participation]).where().eq("event.id", eventId).eq("participant.id", userId).findUnique()
	}
	
	def getParticipationByEventId(eventId: Long, userId: Long = -1): Set[Participation] = {
		var value = Ebean.find(classOf[Participation]).where().eq("event.id", eventId)
		var ret: java.util.Set[Participation] = null
		
		if (userId == -1)
			ret = value.findSet()
		else
			ret = value.ne("participant.id", userId).findSet()
			
		JavaConversions.asScalaSet(ret).toSet
	}

	def save(model: Participation): Unit = {
		if (model != null){
			if (model.getId() == null)
				Ebean.save(model)
			else
				Ebean.update(model)
			var timeBlockList = model.getTimeBlocks()
			if (timeBlockList != null){
				for (i <- 0 until timeBlockList.size()){
					if (timeBlockList.get(i).getId() == null)
						Ebean.save(timeBlockList.get(i))
					else
						Ebean.update(timeBlockList.get(i))
				}
			}
		}
		if (model.getId() == null)
			Ebean.save(model)
		else
			Ebean.update(model)
	}
	
	def deleteTimeBlock(id: Long): TimeBlock = {
		var element = Ebean.find(classOf[TimeBlock], id)
		if (element != null)
			Ebean.delete(element)
		element
	}
	
	def hasFilledOutAvailabilities(eventId: Long, userId: Long): Boolean = {
		val participation = ParticipationsAPI.this.getParticipationById(eventId, userId)
		return (participation.getTimeBlocks().size() != 0)
	}
	
}