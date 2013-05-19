package models

import anorm._
import play.api.Play.current
import play.api.db._

case class Participation(
  participationId: Long,
  status: Participation.Status.Value,
  role: Participation.Role.Value,
  participantId: Long,
  eventId: Long,
  respondedDate: Option[Long]
)

object Participation {
  object Status extends Enumeration {
    val Owner = Value(0)
    val Invited = Value(1)
    val Responded = Value(2)
    val Declined = Value(3)
    val Completed = Value(4)
  }

  object Role extends Enumeration {
    val Participant = Value(0)
    val Important = Value(1)
    val Owner = Value(2)
  }

  def createEntry(status: Status.Value, role: Role.Value, participantId: Long, eventId: Long): Option[Long] = {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Participation(status, role, participantId, eventId, respondedDate) VALUES({status}, {role}, {participantId}, {eventId}, UNIX_TIMESTAMP());")
          .on("status" -> status.id,
              "role" -> role.id,
              "participantId" -> participantId,
              "eventId" -> eventId)
          .executeInsert()
    }
  }

  def createParticipant(role: Role.Value, email: String, eventId: Long): Option[Long] = {
    val userId = User.getUser(email) match {
      case Some(user) =>
        user.userId
      case None =>
        User.create(email).get
    }

    createEntry(Status.Invited, role, userId, eventId)

    // TODO: Send email as well
  }

  def isOwner(userId: Long, eventId: Long): Boolean = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Participation WHERE participantId = {userId} AND eventId = {eventId} AND role = 2;")
          .on("userId" -> userId,
              "eventId" -> eventId)
          .as(SqlResultParser.participation.singleOpt) match {
        case Some(_) =>
          true
        case None =>
          false
      }
    }
  }

  def isParticipant(userId: Long, eventId: Long): Boolean = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Participation WHERE participantId = {userId} AND eventId = {eventId} AND role >= 0;")
          .on("userId" -> userId,
              "eventId" -> eventId)
          .as(SqlResultParser.participation.singleOpt) match {
        case Some(_) =>
          true
        case None =>
          false
      }
    }
  }
}
