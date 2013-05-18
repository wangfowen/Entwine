package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._

import java.util.Date

case class User(
  userId: Long,
  email: String,
  firstName: String,
  lastName: String,
  createdDate: Date,
  joinedDate: Option[Date]
)

object User {
  def createUser(email: String, password: String, firstName: String, lastName: String): Option[Long] = {
    DB.withConnection { implicit c =>
      val userId = SQL("INSERT INTO User(email, password, firstName, lastName, createdDate, joinedDate) VALUES({email}, {password}, {firstName}, {lastName}, NOW(), NOW());")
        .on("email" -> email,
          "password" -> password,
          "firstName" -> firstName,
          "lastName" -> lastName)
        .executeInsert()

      userId
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
}