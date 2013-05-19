package models

import java.util.Date

case class TimeBlock(
  timeBlockId: Long,
  participationId: Long,
  startTime: Date,
  endTime: Date
)

object TimeBlock{

}
