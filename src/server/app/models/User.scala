package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._
import com.roundeights.hasher.Hasher

case class User(
  userId: Long,
  email: String,
  firstName: Option[String],
  lastName: Option[String],
  createdDate: Long,
  joinedDate: Option[Long]
)

object User {
  def register(email: String, password: String, firstName: String, lastName: String): Option[Long] = {
    val hashedPassword = Hasher(password).bcrypt.toString
    DB.withConnection { implicit c =>
      getUser(email) match {
        case Some(User(userId,_,_,_,_,None)) =>
          SQL("UPDATE User SET password = {hashedPassword}, firstName = {firstName}, lastName = {lastName}, joinedDate = UNIX_TIMESTAMP() * 1000 WHERE email = {email};")
              .on("email" -> email,
                  "hashedPassword" -> hashedPassword,
                  "firstName" -> firstName,
                  "lastName" -> lastName)
              .executeUpdate()
          Some(userId)

        case Some(User(_,_,_,_,_,_)) =>
          None

        case None =>
          val userId = SQL("INSERT INTO User(email, password, firstName, lastName, createdDate, joinedDate) VALUES({email}, {hashedPassword}, {firstName}, {lastName}, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);")
              .on("email" -> email,
                  "hashedPassword" -> hashedPassword,
                  "firstName" -> firstName,
                  "lastName" -> lastName)
              .executeInsert()
          userId
      }
    }
  }

  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit c =>
      SQL("SELECT password FROM User WHERE email = {email} AND joinedDate IS NOT NULL;")
          .on("email" -> email)
          .as(scalar[String].singleOpt) match {
        case Some(hashedPassword) =>
          (Hasher(password).bcrypt= hashedPassword) match {
            case true =>
              SQL("SELECT * FROM User WHERE email = {email} AND joinedDate IS NOT NULL;")
                  .on("email" -> email)
                  .as(SqlResultParser.user.singleOpt)
            case false =>
              None
          }
        case None =>
          None
      }
    }
  }

  def create(email: String): Option[Long] = {
    DB.withConnection { implicit c =>
      getUser(email) match {
        case Some(_) =>
          None
        case None =>
          val userId = SQL("INSERT INTO User(email, password, firstName, lastName, createdDate) VALUES({email}, NULL, NULL, NULL, UNIX_TIMESTAMP() * 1000);")
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

  def getUser(userId: Long): Option[User] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM User WHERE userId = {userId};")
          .on("userId" -> userId)
          .as(SqlResultParser.user.singleOpt)
    }
  }
}