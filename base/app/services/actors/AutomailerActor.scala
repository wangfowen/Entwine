package services.actors

import akka.actor.Actor
import services.api._
import database.api._
import database.models._
import application.Global

case class EmailMsg (to: String, subject: String, body: String)

class AutomailerActor extends Actor {
	
	def receive = {
		case EmailMsg(to, subject, body) => {
			try {
				Automailer.sendEmail(to, subject, body)
				println("Successfully sent email to: " + to)
			}
			catch {
				case e: Exception =>
					println("Mailing error: " + e.toString())
			}
		}
		case _ => {
			
		}
	}
	
}