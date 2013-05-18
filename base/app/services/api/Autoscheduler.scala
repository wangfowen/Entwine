package services.api

import database.models._
import scala.collection.mutable._
import java.util.Date
import scala.collection.JavaConversions
import scala.util.control.Breaks._
import scala.annotation.tailrec
import scala.collection.immutable.VectorBuilder

object Autoscheduler {
	
	case class OwnedTimeBlock(owner: EmailAccount, time: TimeBlock)
	
	private implicit def ctbToTb(value: CommonTimeBlock): TimeBlock = {
		new TimeBlock(null, value.getStartTime(), value.getEndTime())
	}
	
	private implicit def tbToCtb(value: (CommonTimeBlock, TimeBlock)): CommonTimeBlock = {
		var ret = value._1
		ret.setStartTime(value._2.getStartTime())
		ret.setEndTime(value._2.getEndTime())
		ret
	}
	
	private implicit object TimeBlockOrdering extends Ordering[OwnedTimeBlock] {
		def compare(tb1: OwnedTimeBlock, tb2: OwnedTimeBlock): Int = {
			tb1.time.compareTo(tb2.time)
		}
	}
	
	private implicit object CommonTimeBlockOrdering extends Ordering[CommonTimeBlock] {
		def compare(tb1: CommonTimeBlock, tb2: CommonTimeBlock): Int = {
			var comp: Long = tb1.getParticipants().size() - tb2.getParticipants().size()
			if (comp > 0)
				-1
			else if (comp < 0)
				1
			else {
				comp = tb1.getDuration() - tb2.getDuration()
				if(comp != 0)
					if(comp < 0) 1 else -1
				else
					tb1.compareTo(tb2)
			}
		}
	}
	
	/**
	 * Entry point into the autoscheduler for external tasks
	 */
	def findCommonTime(event:Event): scala.Seq[CommonTimeBlock] = {
		var participationsList = JavaConversions.asScalaBuffer(event.getParticipationList())
		var arguments = new ListBuffer[OwnedTimeBlock]()

		participationsList.foreach { p =>
			var blockList = JavaConversions.asScalaBuffer(p.getTimeBlocks())
			blockList.foreach { b =>
				arguments.append(OwnedTimeBlock(p.getParticipant(), b))
			}
		}

		var timeList = Autoscheduler.buildCommonList(arguments.sorted(TimeBlockOrdering))
		timeList = timeList.sorted(CommonTimeBlockOrdering)
		
		println(timeList.size)
		timeList
	}
	
	/**
	 * Entry point into the recurseInsertTime function.
	 */
	def buildCommonList(blocklist: Buffer[OwnedTimeBlock]): Vector[CommonTimeBlock] = {
		recurseInsertTime(Vector(), Vector(), blocklist.iterator.buffered)
	}
	
	/**
	 * This function recurses through an ordered list of OwnedTimeBlocks, and uses a buffer to
	 * merge them together.
	 *     pastList: the formalized list of time blocks to be returned
	 *     bufferList: the buffer list that will be replaced every recursion
	 *     current: the current iterator pointing to a timeblock
	 *     
	 *     returns a list of merged timeblocks
	 */
	@tailrec
	private def recurseInsertTime(
			pastList: Vector[CommonTimeBlock],
			bufferList: Vector[CommonTimeBlock],
			current: BufferedIterator[OwnedTimeBlock]): Vector[CommonTimeBlock] = {
		
		if (!current.hasNext)
			return pastList ++ bufferList
		
		var cutoffIndex = 0
		var curBlock = current.next()
		var newBufferList = Vector[CommonTimeBlock]()
		
		breakable {
			bufferList.foreach { item =>
				if(item.getEndTime().compareTo(curBlock.time.getStartTime()) > 0)
					break
				cutoffIndex += 1
			}
		}
		newBufferList = applyTimeBlock(bufferList.slice(cutoffIndex, bufferList.length).iterator, curBlock)
			
		this.recurseInsertTime(
				pastList ++ bufferList.slice(0, cutoffIndex),
				newBufferList,
				current)
	}
	
	/**
	 * This function recursively merges a list of CommonTimeBlock with a lone OwnedTimeBlock, it
	 * recursively resolves the current block in the iterator with the lone block, and passes any
	 * part of the addend which has an end time past the current block onto the next recursion.
	 *     ctbIt: an ORDERED list of CommonTimeBlocks
	 *     addend: the lone time block to be inserted
	 *     
	 *     returns the merged time block list
	 */
	def applyTimeBlock(ctbIt: Iterator[CommonTimeBlock], addend: OwnedTimeBlock): Vector[CommonTimeBlock] = {
		if (!ctbIt.hasNext) {
			var add = new CommonTimeBlock(addend.time)
			add.addParticipant(addend.owner)
			return Vector(add)
		}
		var builder = new VectorBuilder[CommonTimeBlock]()
		
		var operand = ctbIt.next()
		var op1st = operand.getStartTime()
		var op1et = operand.getEndTime()
		var op2st = addend.time.getStartTime()
		var op2et = addend.time.getEndTime()
		
		// direction indicates the position of operand2 relative to operand1:
		//     -1: operand2 is wholly before operand1
		//     0:  operand2 and operand1 intersect
		//     1:  operand2 is wholly after operand1
		var direction = 0
		var equistart = false;
		
		var newOperand1: CommonTimeBlock = null
		var newOperand2: CommonTimeBlock = null
		var newOperand3: CommonTimeBlock = null
		var newAddend: OwnedTimeBlock = null
		
		var comp = op1st.compareTo(op2st) 
		// operand1 begins after operand2
		if (comp > 0) {
			if (op1st.compareTo(op2et) >= 0)
				direction = -1;
			else {
				newOperand1 = new CommonTimeBlock(addend.time)
				newOperand1.addParticipant(addend.owner)
				newOperand1.setEndTime(operand.getStartTime())
				
				newOperand2 = new CommonTimeBlock(operand)
				newOperand2.addParticipant(addend.owner)
			}
		}
		// operand1 begins before operand1
		else if (comp < 0) {
			if (op1et.compareTo(op2st) <= 0)
				direction = 1;
			else {
				newOperand1 = new CommonTimeBlock(operand)
				newOperand1.setEndTime(addend.time.getStartTime())
				
				newOperand2 = new CommonTimeBlock(operand)
				newOperand2.addParticipant(addend.owner)
				newOperand2.setStartTime(addend.time.getStartTime())
			}
		}
		// operand1 and operand2 begin at the same time
		else {
			newOperand1 = new CommonTimeBlock(operand)
			newOperand1.addParticipant(addend.owner)
			equistart = true
		}
		
		// operand1 and operand2 intersect
		if (direction == 0) {
			comp = op1et.compareTo(op2et)
			// operand1 ends after operand2
			if (comp > 0) {
				if (equistart)
					newOperand1.setEndTime(addend.time.getEndTime())
				else
					newOperand2.setEndTime(addend.time.getEndTime())
					
				newOperand3 = new CommonTimeBlock(operand)
				newOperand3.setStartTime(addend.time.getEndTime())
			}
			// operand1 ends before operand2
			else if(comp < 0) {
				if (equistart)
					newOperand1.setEndTime(operand.getEndTime())
				else
					newOperand2.setEndTime(operand.getEndTime())
					
				newAddend = OwnedTimeBlock(addend.owner, new TimeBlock(addend.time))
				
				newAddend.time.setStartTime(operand.getEndTime())
			}
			// operand1 and operand2 end at the same time
			else {
				if (equistart)
					newOperand1.setEndTime(addend.time.getEndTime())
				else
					newOperand2.setEndTime(addend.time.getEndTime())
			}
		}
		// operand1 and operand2 don't intersect
		else {
			if (direction == -1) {
				newOperand1 = new CommonTimeBlock(addend.time)
				newOperand1.addParticipant(addend.owner)
			}
			newOperand2 = new CommonTimeBlock(operand)
			if(direction == 1)
				newAddend = addend
		}
		
		if (newOperand1 != null)
			builder += newOperand1
		if (newOperand2 != null)
			builder += newOperand2
		if (newOperand3 != null)
			builder += newOperand3
		
		// if the addend isn't null, then there are further blocks to merge resolve
		if (newAddend != null)
			builder ++= this.applyTimeBlock(ctbIt, newAddend)
		// otherwise, just tack the remaining blocks on the end
		else
			builder ++= ctbIt
		builder.result()
	}
	
}