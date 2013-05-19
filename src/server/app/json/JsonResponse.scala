package json

import play.api.libs.json.Json

object JsonResponse {
  case class Register(email: String, password: String, firstName: String, lastName: String)
  implicit val register = Json.reads[Register]

  case class Login(email: String, password: String)
  implicit val login = Json.reads[Login]
}
