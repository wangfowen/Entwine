package controllers

import models._
import json.JsonRequest
import json.JsonResponse
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

object AjaxApi extends Controller with Authentication {
  private implicit val userWrite = JsonResponse.user
  private implicit val contactWrite = JsonResponse.contact
  private implicit val eventWrite = JsonResponse.event
  private implicit val participationWrite = JsonResponse.participation
  private implicit val timeBlockWrite = JsonResponse.timeBlock

  def register = Action(BodyParsers.parse.json) { request =>
    request.body.validate[JsonRequest.Register].map { parsed =>
      User.register(parsed.email, parsed.password, parsed.firstName, parsed.lastName).map { userId =>
        Created(Json.obj("userId" -> userId))
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

  def createEvent = TODO

  def getEvent(eventId: Long) = IsAuthenticated { implicit userId => request =>
    HasEventParticipantAccess(eventId) {
      Ok(Json.toJson(Event.get(eventId).get))
    }
  }

  def getEvents = IsAuthenticated { implicit userId => request =>
    val (events, participations) = Event.getAll(userId)
    Ok(Json.obj("events" -> events, "participations" -> participations))
  }
}
