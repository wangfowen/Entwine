package controllers

import play.api.mvc._

trait Authentication {
  private def userId(request: RequestHeader) = request.session.get("userId")
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index)

  def IsAuthenticated(func: => Int => Request[AnyContent] => Result): EssentialAction =
    IsAuthenticated(BodyParsers.parse.anyContent)(func)

  def IsAuthenticated[A](bodyParser: BodyParser[A])(func: => Int => Request[A] => Result) =
    Security.Authenticated(userId, onUnauthorized) { userId => Action(bodyParser)(request => func(userId.toInt)(request)) }
}
