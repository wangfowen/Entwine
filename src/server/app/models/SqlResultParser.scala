package models

import anorm.SqlParser._
import anorm.~

import java.util.Date

object SqlResultParser {
  val user = {
    get[Long]("userId")~
    get[String]("email")~
    get[Option[String]]("firstName")~
    get[Option[String]]("lastName")~
    get[Date]("createdDate")~
    get[Option[Date]]("joinedDate") map {
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
    get[Long]("ownerId")
    get[String]("name")~
    get[String]("description")~
    get[String]("location")~
    get[Date]("createdDate") map {
      case eventId~ownerId~name~description~location~createdDate =>
        Event(eventId, ownerId, name, description, location, createdDate)
    }
  }

  val participation = {
    get[Long]("participationId")~
    get[Int]("status")~
    get[Int]("role")~
    get[Long]("participantId")~
    get[Long]("eventId")~
    get[Date]("respondedDate") map {
      case participationId~status~role~participantId~eventId~respondedDate =>
        Participation(participationId, status, role, participantId, eventId, respondedDate)
    }
  }

  val timeBlock = {
    get[Long]("timeBlockId")~
    get[Long]("participationId")~
    get[Date]("startTime")~
    get[Date]("endTime") map {
      case timeBlockId~participationId~startTime~endTime =>
        TimeBlock(timeBlockId, participationId, startTime, endTime)
    }
  }
}
