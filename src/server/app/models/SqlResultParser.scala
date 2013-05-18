package models

import anorm.SqlParser._
import anorm.~

import java.util.Date

object SqlResultParser {
  val user = {
    get[Long]("userId")~
    get[String]("email")~
    get[String]("firstName")~
    get[String]("lastName")~
    get[Date]("createdDate")~
    get[Option[Date]]("joinedDate") map {
      case userId~email~firstName~lastName~createdDate~joinedDate =>
        User(userId, email, firstName, lastName, createdDate, joinedDate)
    }
  }
}