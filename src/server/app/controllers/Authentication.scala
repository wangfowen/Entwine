package controllers

import play.api.mvc._

trait Authentication {
  private def userId(request: RequestHeader) = request.session.get("userId")
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index)

  def isAuthenticated(func: => Int => Request[AnyContent] => Result): EssentialAction =
    isAuthenticated(BodyParsers.parse.anyContent)(func)

  def isAuthenticated[A](bodyParser: BodyParser[A])(func: => Int => Request[A] => Result) =
    Security.Authenticated(userId, onUnauthorized) { userId => Action(bodyParser)(request => func(userId.toInt)(request)) }
}
