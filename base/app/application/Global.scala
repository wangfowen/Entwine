

package application

import play.api._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor._
import akka.actor.Actors._
import services.actors._
import java.text.SimpleDateFormat
import play.api.i18n.Messages

object Global extends GlobalSettings {
	
	def MIN_PARTICIPATION_RATIO: Double = 0.75
	def NUM_DISPLAY_COMMON_TIME: Int = 3
	def DATE_FORMATTER: SimpleDateFormat = new SimpleDateFormat(Messages("scheduler.timeFormat"))

	var autoschedulerActor: ActorRef = null
	var automailerActor: ActorRef = null
	
	override def onStart (app: Application) {
		autoschedulerActor = Akka.system.actorOf(Props[AutoschedulerActor], name = "auto-scheduler")
		automailerActor = Akka.system.actorOf(Props[AutomailerActor], "auto-mailer")
		println("Application: Started services actors")
	}
	
	override def onStop (app: Application) {
		Akka.system.stop(autoschedulerActor)
		Akka.system.stop(automailerActor)
		println("Application: Stopped services actors.")
	}
	
}