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

case class TimeBlockStrength(
  startTime: Long,
  endTime: Long,
  strength: Int
)

object TimeBlock{
  def update(participationId: Long, timeBlocks: List[JsonRequest.TimeBlock]) {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM TimeBlock WHERE participationId = {participationId};").execute()

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

  def getTimeBlockStrength(userId: Long, eventId: Long): List[TimeBlockStrength] = {
    DB.withConnection { implicit c =>
      val participationId = Participation.getParticipationId(userId, eventId).get
      val timeBlocks = get(participationId)

      // Set up the initial map
      var strength = Map[Long, Int]()
      timeBlocks.foreach { timeBlock =>
        val intervalCount = ((timeBlock.endTime - timeBlock.startTime)/900000).toInt


        var i = 0;
        for (i <- 0 to (intervalCount-1)) {
          strength += ((timeBlock.startTime + i*900000) -> 0)
        }
      }

      // Fill in the map with participant's timeBlocks
      var max = 0;
      val participants = SQL("SELECT T.* FROM TimeBlock T, Participation P WHERE T.participationId = P.participationId AND P.eventId = {eventId} AND T.participationId <> {participationId};")
          .on("eventId" -> eventId,
              "participationId" -> participationId)
          .as(SqlResultParser.timeBlock *)
      participants.foreach { timeBlock =>
        val intervalCount = ((timeBlock.endTime - timeBlock.startTime)/900000).toInt

        var i = 0;
        for (i <- 0 to (intervalCount-1)) {
          val currentInterval = (timeBlock.startTime + i*900000)
          if (timeBlocks.contains(currentInterval))
            strength += (currentInterval -> (strength(currentInterval) + 1))

          if (strength(currentInterval) > max)
            max = strength(currentInterval)
        }
      }

      // Create TimeBlockStrength entries
      val timeBlockStrength = strength.map { pair =>
        TimeBlockStrength(pair._1, pair._1 + 900000, (pair._2.toDouble / max * 10).toInt)
      }

      timeBlockStrength.toList
    }
  }
}
