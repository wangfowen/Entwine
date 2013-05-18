package events

import org.specs2.mutable.Specification
import database.models._
import java.util.Date
import services.api.Autoscheduler
import services.api.Autoscheduler.OwnedTimeBlock
import scala.collection.mutable.ListBuffer

class AutomationSpec extends Specification {
	
	private implicit def cntb(value: (Int, Int)): TimeBlock = {
		new TimeBlock(null, new Date(value._1), new Date(value._2))
	}
	
	private implicit def toCTB1(value: (Int, Int, EmailAccount)): CommonTimeBlock = {
		var ret = new CommonTimeBlock(new TimeBlock(null, new Date(value._1), new Date(value._2)))
		ret.addParticipant(value._3)
		ret
	}
	
	private implicit def toCTB2(value: (Int, Int, EmailAccount, EmailAccount)): CommonTimeBlock = {
		var ret = new CommonTimeBlock(new TimeBlock(null, new Date(value._1), new Date(value._2)))
		ret.addParticipant(value._3)
		ret.addParticipant(value._4)
		ret
	}
	
	private implicit def toCTB1(value: (Int, Int, EmailAccount, EmailAccount, EmailAccount)): CommonTimeBlock = {
		var ret = new CommonTimeBlock(new TimeBlock(null, new Date(value._1), new Date(value._2)))
		ret.addParticipant(value._3)
		ret.addParticipant(value._4)
		ret.addParticipant(value._5)
		ret
	}
	
	private implicit def toCTB1(value: (Int, Int, EmailAccount, EmailAccount, EmailAccount, EmailAccount)): CommonTimeBlock = {
		var ret = new CommonTimeBlock(new TimeBlock(null, new Date(value._1), new Date(value._2)))
		ret.addParticipant(value._3)
		ret.addParticipant(value._4)
		ret.addParticipant(value._5)
		ret.addParticipant(value._6)
		ret
	}
	
	private def cntbn1(value: Int): TimeBlock = {
		new TimeBlock(null, null, new Date(value))
	}
	
	private def cntbn2(value: Int): TimeBlock = {
		new TimeBlock(null, new Date(value), null)
	}
	
	
	private def usrAcc = Seq[EmailAccount](
		new EmailAccount("Bob"),
		new EmailAccount("Sam"),
		new EmailAccount("Alicia"),
		new EmailAccount("Vivian"),
		new EmailAccount("Robert")
	)
	
	
	def compareTimeBlockLists(expected: Seq[CommonTimeBlock], results: Seq[CommonTimeBlock]): Boolean = {
		var count = 0;
		
		(expected, results).zipped.foreach((a, b) => {
			if (a.equals(b))
				count += 1
		})
		
		count mustEqual expected.length
	}
	
	"The applyTimeBlock function" should {
		"handle single non-intersecting case" in {
			var baselist = Seq[CommonTimeBlock](
				(10, 20, usrAcc(0))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 20, usrAcc(0)),
				(30, 40, usrAcc(1))
			)
			var insert = OwnedTimeBlock(usrAcc(1), (30, 40))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle single pre-intersect case" in {
			var baselist = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 20, usrAcc(1)),
				(20, 30, usrAcc(0), usrAcc(1)),
				(30, 40, usrAcc(0))
			)
			var insert = OwnedTimeBlock(usrAcc(1), (10, 30))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle single post-intersect case" in {
			var baselist = Seq[CommonTimeBlock](
				(10, 30, usrAcc(0))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 20, usrAcc(0)),
				(20, 30, usrAcc(0), usrAcc(1)),
				(30, 40, usrAcc(1))
			)
			var insert = OwnedTimeBlock(usrAcc(1), (20, 40))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle single left-surround-intersect case" in {
			var baselist = Seq[CommonTimeBlock](
				(10, 80, usrAcc(0))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 20, usrAcc(0)),
				(20, 40, usrAcc(0), usrAcc(1)),
				(40, 80, usrAcc(0))
			)
			var insert = OwnedTimeBlock(usrAcc(1), (20, 40))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle single right-surround-intersect case" in {
			var baselist = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 20, usrAcc(1)),
				(20, 40, usrAcc(0), usrAcc(1)),
				(40, 80, usrAcc(1))
			)
			var insert = OwnedTimeBlock(usrAcc(1), (10, 80))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle equivalence case" in {
			var baselist = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0))
			)
			var expected = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0), usrAcc(1))
			)
			var insert = OwnedTimeBlock(usrAcc(1), (20, 40))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle simple multi-user case" in {
			var baselist = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0), usrAcc(2), usrAcc(3))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 20, usrAcc(1)),
				(20, 40, usrAcc(0), usrAcc(1), usrAcc(2), usrAcc(3)),
				(40, 100, usrAcc(1))
			)
			var insert = OwnedTimeBlock(usrAcc(1), (10, 100))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle small multi-collision case" in {
			var baselist = Seq[CommonTimeBlock](
				(10, 40, usrAcc(0), usrAcc(1)),
				(60, 80, usrAcc(0), usrAcc(2))
			)
			
			var expected = Seq[CommonTimeBlock](
				(10, 30, usrAcc(0), usrAcc(1)),
				(30, 40, usrAcc(0), usrAcc(1), usrAcc(3)),
				(40, 60, usrAcc(3)),
				(60, 70, usrAcc(0), usrAcc(2), usrAcc(3)),
				(70, 80, usrAcc(0), usrAcc(2))
			)
			var insert = OwnedTimeBlock(usrAcc(3), (30, 70))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle large multi-collision case" in {
			var baselist = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0), usrAcc(1)),
				(60, 80, usrAcc(0), usrAcc(2)),
				(120, 180, usrAcc(2)),
				(200, 210, usrAcc(1))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 20, usrAcc(3)),
				(20, 40, usrAcc(0), usrAcc(1), usrAcc(3)),
				(40, 60, usrAcc(3)),
				(60, 80, usrAcc(0), usrAcc(2), usrAcc(3)),
				(80, 120, usrAcc(3)),
				(120, 180, usrAcc(2), usrAcc(3)),
				(180, 200, usrAcc(3)),
				(200, 210, usrAcc(1))
			)
			var insert = OwnedTimeBlock(usrAcc(3), (10, 200))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
		
		"handle large non-collision case" in {
			var baselist = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0), usrAcc(1)),
				(60, 80, usrAcc(0), usrAcc(2)),
				(120, 180, usrAcc(2)),
				(200, 210, usrAcc(1))
			)
			var expected = Seq[CommonTimeBlock](
				(20, 40, usrAcc(0), usrAcc(1)),
				(60, 80, usrAcc(0), usrAcc(2)),
				(100, 110, usrAcc(3)),
				(120, 180, usrAcc(2)),
				(200, 210, usrAcc(1))
			)
			var insert = OwnedTimeBlock(usrAcc(3), (100, 110))
			
			compareTimeBlockLists(expected, Autoscheduler.applyTimeBlock(baselist.iterator, insert))
		}
	}
	
	"The buildCommonList function" should {
		"handle a simple merging case" in {
			var insert = scala.collection.mutable.Buffer[OwnedTimeBlock](
				OwnedTimeBlock(usrAcc(0), (10, 50)),
				OwnedTimeBlock(usrAcc(1), (30, 70))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 30, usrAcc(0)),
				(30, 50, usrAcc(0), usrAcc(1)),
				(50, 70, usrAcc(1))
			)
			
			compareTimeBlockLists(expected, Autoscheduler.buildCommonList(insert))
		}
		
		"handle a simple non-colliding case" in {
			var insert = scala.collection.mutable.Buffer[OwnedTimeBlock](
				OwnedTimeBlock(usrAcc(0), (10, 50)),
				OwnedTimeBlock(usrAcc(1), (80, 110)),
				OwnedTimeBlock(usrAcc(0), (500, 600))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 50, usrAcc(0)),
				(80, 110, usrAcc(1)),
				(500, 600, usrAcc(0))
			)
			
			compareTimeBlockLists(expected, Autoscheduler.buildCommonList(insert))
		}
		
		"handle a more complex mixed case" in {
			var insert = scala.collection.mutable.Buffer[OwnedTimeBlock](
				OwnedTimeBlock(usrAcc(0), (10, 50)),
				OwnedTimeBlock(usrAcc(3), (40, 100)),
				OwnedTimeBlock(usrAcc(4), (50, 100)),
				OwnedTimeBlock(usrAcc(1), (80, 200)),
				OwnedTimeBlock(usrAcc(3), (100, 150)),
				OwnedTimeBlock(usrAcc(2), (400, 1800)),
				OwnedTimeBlock(usrAcc(1), (500, 600))
			)
			var expected = Seq[CommonTimeBlock](
				(10, 40, usrAcc(0)),
				(40, 50, usrAcc(0), usrAcc(3)),
				(50, 80, usrAcc(3), usrAcc(4)),
				(80, 100, usrAcc(1), usrAcc(3), usrAcc(4)),
				(100, 150, usrAcc(1), usrAcc(3)),
				(150, 200, usrAcc(1)),
				(400, 500, usrAcc(2)),
				(500, 600, usrAcc(1), usrAcc(2)),
				(600, 1800, usrAcc(2))
			)
			
			compareTimeBlockLists(expected, Autoscheduler.buildCommonList(insert))
		}
	}
	
	"The overall automatic time selector" should {
		"deal with a basic case" in {
			var head = (64, 70, usrAcc(0), usrAcc(1))
			var p1 = new Participation()
			var p2 = new Participation()
			var e1 = new Event()
			
			var expected = Seq[CommonTimeBlock](
				(64, 70, usrAcc(0), usrAcc(1)),
				(95, 100, usrAcc(1))
			)
			
			p1.addTimeBlock((50, 70))
			p2.addTimeBlock((64, 73))
			
			p1.setParticipant(usrAcc(0))
			p2.setParticipant(usrAcc(1))
			
			e1.addParticipation(p1)
			e1.addParticipation(p2)
			
			var ret = Autoscheduler.findCommonTime(e1)
			
			(ret.head.compareTo(head) mustEqual 0)
		}
		
		"deal with a non colliding case" in {
			var p1 = new Participation()
			var p2 = new Participation()
			var e1 = new Event()
			
			var expected = Seq[CommonTimeBlock](
				(50, 80, usrAcc(0)),
				(95, 100, usrAcc(1))
			)
			
			p1.addTimeBlock((50, 80))
			p2.addTimeBlock((95, 100))
			
			p1.setParticipant(usrAcc(0))
			p2.setParticipant(usrAcc(1))
			
			e1.addParticipation(p1)
			e1.addParticipation(p2)
			
			compareTimeBlockLists(expected, Autoscheduler.findCommonTime(e1))
		}
//		
//		"deal with multiple collisions on multiple strands" in {
//			var p1 = new Participation()
//			var p2 = new Participation()
//			var e1 = new Event()
//			
//			p1.addTimeBlock((50, 80))
//			p1.addTimeBlock((90, 115))
//			p2.addTimeBlock((30, 55))
//			p2.addTimeBlock((100, 150))
//			e1.addParticipation(p1)
//			e1.addParticipation(p2)
//			
//			var ret = Autoscheduler.findCommonTime(e1)
//			var expected = Seq[TimeBlock]((100, 115), (50, 55))
//			
//			compareTimeBlockLists(expected, ret)
//		}
//		
//		"deal with multiple collisions on single strand of time" in {
//			var p1 = new Participation()
//			var p2 = new Participation()
//			var e1 = new Event()
//			
//			p1.addTimeBlock((100, 1000))
//			p1.addTimeBlock((1150, 1400))
//			p2.addTimeBlock((725, 995))
//			p2.addTimeBlock((30, 165))
//			p2.addTimeBlock((999, 1500))
//			p2.addTimeBlock((350, 600))
//			e1.addParticipation(p1)
//			e1.addParticipation(p2)
//			
//			var ret = Autoscheduler.findCommonTime(e1)
//			var expected = List[TimeBlock]((725, 995), (350, 600), (1150, 1400), (100, 165), (999, 1000))
//			
//			compareTimeBlockLists(expected, ret)
//		}
//		
//		"deal with edge cases" in {
//			var p1 = new Participation()
//			var p2 = new Participation()
//			var e1 = new Event()
//			
//			p1.addTimeBlock((100, 110))
//			p2.addTimeBlock((90, 100))
//			p2.addTimeBlock((110, 120))
//			e1.addParticipation(p1)
//			e1.addParticipation(p2)
//			
//			var ret = Autoscheduler.findCommonTime(e1)
//			
//			(ret.length mustEqual 0)
//		}
	}
	
}