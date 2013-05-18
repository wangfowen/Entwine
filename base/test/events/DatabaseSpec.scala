package events

import java.io.File
import java.util.HashMap;

import play.api.test.Helpers.running
import play.api.test.FakeApplication

import database.models._
import com.avaje.ebean.Ebean
import org.specs2.mutable._

class DatabaseSpec extends Specification {
//	"The fake application" should {
//		"load the local configuration file correctly" in {
//			var file = new File("conf/application.tlei.conf")
//			file.exists() must_==(true)
//		}
//	}
//	"Ebeans" should {
//		"save a new account object" in {
//			running(FakeApplication()){
//				var tempAccount = new Account()
//				tempAccount.setEmail("yourmom@gmail.com")
//				Ebean.save(tempAccount)
//				tempAccount.getId() must_!=(null)
//			}
//		}
//		"save a nested event object" in {
//			running(FakeApplication()){
//				var tempEvent = new Event()
//				var tempAccount = new Account()
//				tempAccount.setEmail("anemail@gmail.com")
//				tempEvent.setOwner(tempAccount)
//				tempEvent.setName("randomevent")
//				Ebean.save(tempAccount)
//				Ebean.save(tempEvent)
//				tempAccount.getId() must_!=(null)
//				tempEvent.getId() must_!=(null)
//			}
//		}
//		"load a nested event object" in {
//			running(FakeApplication()){
//				var tempEvent = Ebean.find(classOf[Event], 41)
//				tempEvent must_!=(null)
//				tempEvent.getOwner() must_!=(null)
//				tempEvent.getOwner().getId() must_!=(null)
//			}
//		}
//	}
}