package controllers

import models._
import json.JsonRequest
import json.JsonResponse
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import smtp.SmtpScheduler
import smtp.NewInvite

object AjaxApi extends Controller with Authentication {
  private implicit val userWrite = JsonResponse.user
  private implicit val contactWrite = JsonResponse.contact
  private implicit val eventWrite = JsonResponse.event
  private implicit val participationWrite = JsonResponse.participation
  private implicit val timeBlockWrite = JsonResponse.timeBlock

  def register = Action(BodyParsers.parse.json) { request =>
    request.body.validate[JsonRequest.Register].map { parsed =>
      User.register(parsed.email, parsed.password, parsed.firstName, parsed.lastName).map { userId =>
        Created(Json.obj("userId" -> userId)).withSession("userId" -> userId.toString)
      }.getOrElse(BadRequest)
    }.getOrElse(BadRequest)
  }

  def login = Action(BodyParsers.parse.json) { request =>
    request.body.validate[JsonRequest.Login].map { parsed =>
      User.authenticate(parsed.email, parsed.password).map { user =>
        Ok(Json.toJson(user)).withSession("userId" -> user.userId.toString)
      }.getOrElse(BadRequest)
    }.getOrElse(BadRequest)
  }

  def logout = Action { _ =>
    Ok(Json.obj()).withNewSession
  }

  def getContacts = IsAuthenticated { userId => request =>
    Ok(Json.toJson(Contact.get(userId)))
  }

  def createEvent = IsAuthenticated(BodyParsers.parse.json) { implicit userId => implicit request =>
    request.body.validate[JsonRequest.CreateEvent].map { parsed =>
      val eventId = Event.create(userId, parsed.name, parsed.description, parsed.location).get
      parsed.participants.foreach { p =>
        Participation.createParticipant(Participation.Role(p.role), p.email, eventId).get
        val user = User.getUser(p.email).get
        Contact.create(userId, user.userId)
        Application.smtpScheduler.tell(
            NewInvite(
                user.email,
                controllers.scheduler.routes.SelectTime.index(eventId, user.userId).absoluteURL(),
                User.getUser(userId).get))
      }
      Ok(Json.obj("eventId" -> eventId))
    }.getOrElse(BadRequest)
  }

  def getEvent(eventId: Long) = IsAuthenticated { implicit userId => request =>
    HasEventParticipantAccess(eventId) {
      val (event, participants) = Event.getEventsAndParticipants(eventId)
      Ok(Json.obj("event" -> event, "participants" -> participants))
    }
  }

  def getEvents = IsAuthenticated { implicit userId => request =>
    val (events, participations) = Event.getAll(userId)
    Ok(Json.obj("events" -> events, "participations" -> participations))
  }

  def getTimeBlocks(participationId: Long) = IsAuthenticated { implicit userId => request =>
    // TODO: Check for access
    Ok(Json.toJson(TimeBlock.get(participationId)))
  }

  def saveTimeBlocks = Action(BodyParsers.parse.json) { implicit request =>
    request.body.validate[JsonRequest.SaveTimeBlocks].map { parsed =>
      implicit val userId: Long = request.session.get("userId").map(_.toLong).getOrElse(parsed.userId.getOrElse(-1))
      HasEventParticipantAccess(parsed.eventId) {
        TimeBlock.update(userId, parsed.eventId, parsed.timeBlocks)
        Ok("{}")
      }
    }.getOrElse(BadRequest)
  }
}
