package json

import play.api.libs.json.Json

object JsonRequest {
  case class Register(email: String, password: String, firstName: String, lastName: String)
  implicit val register = Json.reads[Register]

  case class Login(email: String, password: String)
  implicit val login = Json.reads[Login]

  case class Participant(email: String, role: Int)
  implicit val participant = Json.reads[Participant]

  case class CreateEvent(name: String, description: String, location: String, participants: List[Participant])
  implicit val createEvent = Json.reads[CreateEvent]
}
