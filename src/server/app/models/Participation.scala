package models

import anorm._
import play.api.Play.current
import play.api.db._

import java.util.Date

case class Participation(
  participationId: Long,
  status: Participation.Status.Value,
  role: Participation.Role.Value,
  participantId: Long,
  eventId: Long,
  respondedDate: Date
)

object Participation {
  object Status extends Enumeration {
    val Owner = Value(0)
    val Invited = Value(1)
    val Responded = Value(2)
    val Declined = Value(3)
  }

  object Role extends Enumeration {
    val Owner = Value(0)
    val Important = Value(1)
    val Participant = Value(2)
  }

  def create(status: Status.Value, role: Role.Value, participantId: Long, eventId: Long): Option[Long] = {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Participation(status, role, participantId, eventId, respondedDate) VALUES({status}, {role}, {participantId}, {eventId}, NOW());")
          .on("status" -> status.id,
              "role" -> role.id,
              "participantId" -> participantId,
              "eventId" -> eventId)
          .executeInsert()
    }
  }
}
