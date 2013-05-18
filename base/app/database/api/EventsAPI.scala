package database.api

import java.util.Date
import database.models._
import com.avaje.ebean.Ebean

object EventsAPI {
	
	def bindEventData(valueMap: Map[String, Any], autoCreate: Boolean = false): Event = {
		var event = new Event()
		if(valueMap.contains("id")){
			var id = valueMap("id").asInstanceOf[Int]
			event = Ebean.find(classOf[Event], id);
			if(event == null && !autoCreate)
				throw new IllegalArgumentException("Cannot find event with id: " + id)
		}

		event.setName(if(valueMap.contains("name")) valueMap("name").asInstanceOf[String] else event.getName())
		event.setDescription(if(valueMap.contains("description")) valueMap("description").asInstanceOf[String] else event.getDescription())
		event.setLocation(if(valueMap.contains("location")) valueMap("location").asInstanceOf[String] else event.getLocation())
		event.setStartDate(if(valueMap.contains("startDate")) valueMap("startDate").asInstanceOf[Date] else event.getStartDate())
		event.setEndDate(if(valueMap.contains("endDate")) valueMap("endDate").asInstanceOf[Date] else event.getEndDate())
		event.setCutoffDate(if(valueMap.contains("cutoffDate")) valueMap("cutoffDate").asInstanceOf[Date] else event.getCutoffDate())
		event
	}

	def save(model: Event) = {
		if(model != null){
			if(model.getId() == null)
				Ebean.save(model)
			else
				Ebean.update(model)
			var participationList = model.getParticipationList()
			for(i <- 0 until participationList.size()){
				if(participationList.get(i).getId() == null)
					Ebean.save(participationList.get(i))
				else
					Ebean.update(participationList.get(i))
			}
			
			var commonTimeBlockList = model.getCommonTimeBlockList()
			for(i <- 0 until commonTimeBlockList.size()){
				if(commonTimeBlockList.get(i).getId() == null)
					Ebean.save(commonTimeBlockList.get(i))
				else
					Ebean.update(commonTimeBlockList.get(i))
			}
		}
	}
	
	def removeCommonTimeBlocks(model: Event) {
		var ctbl = model.getCommonTimeBlockList()
		for(i <- 0 until ctbl.size())
			Ebean.delete(ctbl.get(i))
		ctbl.clear();
	}

	def getEventById(id: Long): Event = {
		if(id == -1)
			Ebean.find(classOf[Event]).findList().get(0)
		else
			Ebean.find(classOf[Event], id)
	}
	
}