package models

import anorm._
import json._
import play.api.Play.current
import play.api.db._

case class TimeBlock(
  timeBlockId: Long,
  participationId: Long,
  startTime: Long,
  endTime: Long
)

object TimeBlock{
  def update(participationId: Long, timeBlocks: List[JsonRequest.TimeBlock]) {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM TimeBlock WHERE participationId = {participationId};")
          .on("participationId" -> participationId)
          .executeUpdate()

      timeBlocks foreach { block =>
        SQL("INSERT INTO TimeBlock(participationId, startTime, endTime) VALUES({participationId}, {startTime}, {endTime});")
            .on("participationId" -> participationId,
                "startTime" -> block.startTime,
                "endTime" -> block.endTime)
            .executeInsert()
      }
    }
  }

  def update(userId: Long, eventId: Long, timeBlocks: List[JsonRequest.TimeBlock]) {
    Participation.getParticipationId(userId, eventId).map { participationId =>
      update(participationId, timeBlocks)
    }
  }

  def get(participationId: Long): List[TimeBlock] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM TimeBlock WHERE participationId = {participationId};")
          .on("participationId" -> participationId)
          .as(SqlResultParser.timeBlock *)
    }
  }
}
