package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._

import java.util.Date

case class User(
  userId: Long,
  email: String,
  firstName: Option[String],
  lastName: Option[String],
  createdDate: Date,
  joinedDate: Option[Date]
)

object User {
  def register(email: String, password: String, firstName: String, lastName: String): Option[Long] = {
    DB.withConnection { implicit c =>
      getUser(email) match {
        case Some(User(userId,_,_,_,_,None)) =>
          SQL("UPDATE User SET password = {password}, firstName = {firstName}, lastName = {lastName}, joinedDate = NOW() WHERE email = {email};")
              .on("email" -> email,
                  "password" -> password,
                  "firstName" -> firstName,
                  "lastName" -> lastName)
              .executeUpdate()
          Some(userId)

        case Some(User(_,_,_,_,_,_)) =>
          None

        case None =>
          val userId = SQL("INSERT INTO User(email, password, firstName, lastName, createdDate, joinedDate) VALUES({email}, {password}, {firstName}, {lastName}, NOW(), NOW());")
              .on("email" -> email,
                  "password" -> password,
                  "firstName" -> firstName,
                  "lastName" -> lastName)
              .executeInsert()
          userId
      }
    }
  }

  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM User WHERE email = {email} AND password = {password} AND joinedDate IS NOT NULL;")
          .on("email" -> email,
              "password" -> password)
          .as(SqlResultParser.user.singleOpt)
    }
  }

  def create(email: String): Option[Long] = {
    DB.withConnection { implicit c =>
      getUser(email) match {
        case Some(_) =>
          None
        case None =>
          val userId = SQL("INSERT INTO User(email, createdDate) VALUES({email}, NOW());")
            .on("email" -> email)
            .executeInsert()
          userId
      }
    }
  }

  def getUser(email: String): Option[User] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM User WHERE email = {email};")
        .on("email" -> email)
        .as(SqlResultParser.user.singleOpt)
    }
  }
}