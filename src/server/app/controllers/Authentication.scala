package controllers

import play.api.mvc._
import models.Participation

trait Authentication {
  private def userId(request: RequestHeader) = request.session.get("userId")
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index)

  def IsAuthenticated(func: => Long => Request[AnyContent] => Result): EssentialAction =
    IsAuthenticated(BodyParsers.parse.anyContent)(func)

  def IsAuthenticated[A](bodyParser: BodyParser[A])(func: => Long => Request[A] => Result) =
    Security.Authenticated(userId, onUnauthorized) { userId => Action(bodyParser)(request => func(userId.toLong)(request)) }



  def HasEventOwnerAccess(eventId: Long)(func: => Result)(implicit userId: Long) = {
    Participation.isOwner(userId, eventId) match {
      case true =>
        func
      case false =>
        Results.Forbidden
    }
  }

  def HasEventParticipantAccess(eventId: Long)(func: => Result)(implicit userId: Long) = {
    Participation.isParticipant(userId, eventId) match {
      case true =>
        func
      case false =>
        Results.Forbidden
    }
  }
}
