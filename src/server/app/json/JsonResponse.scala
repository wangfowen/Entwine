package json

import models._
import play.api.libs.json.Json

object JsonResponse {
  implicit val user = Json.writes[User]
  implicit val contact = Json.writes[Contact]
  implicit val event = Json.writes[Event]
  implicit val participation = Json.writes[Participation]
  implicit val timeBlock = Json.writes[TimeBlock]
}
