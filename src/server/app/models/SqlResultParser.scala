package models

import anorm.SqlParser._
import anorm.~

object SqlResultParser {
  val user = {
    get[Long]("userId")~
    get[String]("email")~
    get[Option[String]]("firstName")~
    get[Option[String]]("lastName")~
    get[Long]("createdDate")~
    get[Option[Long]]("joinedDate") map {
      case userId~email~firstName~lastName~createdDate~joinedDate =>
        User(userId, email, firstName, lastName, createdDate, joinedDate)
    }
  }

  val contact = {
    get[Long]("userId")~
    get[Long]("contactId") map {
      case userId~contactId =>
        Contact(userId, contactId)
    }
  }

  val event = {
    get[Long]("eventId")~
    get[String]("name")~
    get[String]("description")~
    get[String]("location")~
    get[Long]("ownerId")~
    get[Long]("createdDate") map {
      case eventId~name~description~location~ownerId~createdDate =>
        Event(eventId, name, description, location, ownerId, createdDate)
    }
  }

  val participation = {
    get[Long]("participationId")~
    get[Int]("status")~
    get[Int]("role")~
    get[Long]("participantId")~
    get[Long]("eventId")~
    get[Option[Long]]("respondedDate") map {
      case participationId~status~role~participantId~eventId~respondedDate =>
        Participation(participationId, Participation.Status(status), Participation.Role(role), participantId, eventId, respondedDate)
    }
  }

  val timeBlock = {
    get[Long]("timeBlockId")~
    get[Long]("participationId")~
    get[Long]("startTime")~
    get[Long]("endTime") map {
      case timeBlockId~participationId~startTime~endTime =>
        TimeBlock(timeBlockId, participationId, startTime, endTime)
    }
  }
}
