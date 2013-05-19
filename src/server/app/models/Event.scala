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

  def getAll(userId: Long): (List[Event], List[Participation]) = {
    DB.withConnection { implicit c =>
      val events = SQL("SELECT E.* FROM Event E, Participation P WHERE E.eventId = P.eventId AND P.participantId = {userId} ORDER BY E.eventId ASC;")
          .on("userId" -> userId)
          .as(SqlResultParser.event *)
      val participations = SQL("SELECT P.* FROM Event E, Participation P WHERE E.eventId = P.eventId AND P.participantId = {userId} ORDER BY E.eventId ASC;")
          .on("userId" -> userId)
          .as(SqlResultParser.participation *)
      (events, participations)
    }
  }

  /*
   * Determine if the userId owns eventId.
   * Deprecated (To be consistent with isParticipant); Use Participation.isOwner instead.
   */
  @Deprecated
  def isOwner(userId: Long, eventId: Long): Boolean = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Event WHERE eventId = {eventId} AND ownerId = {ownerId};")
          .on("eventId" -> eventId,
              "ownerId" -> userId)
          .as(SqlResultParser.event.singleOpt) match {
        case Some(_) =>
          true
        case None =>
          false
      }
    }
  }
}
