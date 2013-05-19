package models

import java.util.Date

case class Event(
  eventId: Long,
  ownerId: Long,
  name: String,
  description: String,
  location: String,
  createdDate: Date
)

object Event {
  def create(ownerId: Long, name: String, description: String, location: String, participants: List[String]): Option[Event] = {

  }


}
