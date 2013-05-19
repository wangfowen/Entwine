package models

import anorm._
import play.api.Play.current
import play.api.db._

case class Contact(
  userId: Long,
  contactId: Long
)

object Contact {
  def create(userId: Long, contactId: Long) {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Contact WHERE userId = {userId} AND contactId = {contactId};")
          .on("userId" -> userId,
              "contactId" -> contactId)
          .as(SqlResultParser.contact.singleOpt) match {
        case Some(_) =>

        case None =>
          SQL("INSERT INTO Contact(userId, contactId) VALUES({userId}, {contactId});")
              .on("userId" -> userId,
                  "contactId" -> contactId)
              .executeInsert()
      }
    }
  }

  def get(userId: Long): List[Contact] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Contact WHERE userId = {userId};")
          .on("userId" -> userId)
          .as(SqlResultParser.contact *)
    }
  }
}