package models

import anorm._
import play.api.Play.current
import play.api.db._

import java.util.Date

case class TimeBlock(
  timeBlockId: Long,
  participationId: Long,
  startTime: Date,
  endTime: Date
)

object TimeBlock{
  def update(participationId: Long, timeBlock: List[(Date, Date)]) = {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM TimeBlock WHERE participationId = {participationId};").execute()

      timeBlock foreach { block =>
        SQL("INSERT INTO TimeBlock(participationId, startTime, endTime) VALUES({participationId}, {startTime}, {endTime});")
            .on("participationId" -> participationId,
                "startTime" -> block._1,
                "endTime" -> block._1)
            .executeInsert()
      }
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
