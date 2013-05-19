package json

import models._
import play.api.libs.json.{JsNumber, JsValue, Writes, Json}

import java.util.Date

object JsonResponse {
  implicit val participationStatusEnum = new Writes[Participation.Status.Value] {
    def writes(enum: Participation.Status.Value): JsValue = JsNumber(enum.id)
  }
  implicit val participationRoleEnum = new Writes[Participation.Role.Value] {
    def writes(enum: Participation.Role.Value): JsValue = JsNumber(enum.id)
  }

  implicit val dateToLong = new Writes[Date] {
    def writes(date: Date): JsValue = JsNumber(date.getTime)
  }

  implicit val user = Json.writes[User]
  implicit val contact = Json.writes[Contact]
  implicit val event = Json.writes[Event]
  implicit val participation = Json.writes[Participation]
  implicit val timeBlock = Json.writes[TimeBlock]
}
