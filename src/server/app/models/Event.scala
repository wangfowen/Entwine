package models

import anorm._
import play.api.Play.current
import play.api.db._

import java.util.Date

case class Event(
  eventId: Long,
  name: String,
  description: String,
  location: String,
  ownerId: Long,
  createdDate: Date
)

object Event {
  def create(ownerId: Long, name: String, description: String, location: String): Option[Long] = {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Event(name, description, location, ownerId, createdDate) VALUES({name}, {description}, {location}, {ownerId}, NOW());")
          .on("name" -> name,
              "description" -> description,
              "location" -> location,
              "ownerId" -> ownerId)
          .executeInsert()
    }
  }

  def get(eventId: Long): Option[Event] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Event WHERE eventId = {eventId};")
          .on("eventId" -> eventId)
          .as(SqlResultParser.event.singleOpt)
    }
  }

  def getAll(ownerId: Long): List[Event] = {
    DB.withConnection {implicit c =>
      SQL("SELECT * FROM Event WHERE ownerId = {ownerId};")
          .on("ownerId" -> ownerId)
          .as(SqlResultParser.event *)
    }
  }

}
