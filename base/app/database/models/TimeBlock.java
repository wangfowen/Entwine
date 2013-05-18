package database.models;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.time.DateUtils;

@Entity
@Table(name="time_block")
public class TimeBlock implements Comparable<TimeBlock> {
	@Id
	private Long id;

	@ManyToOne
	@JoinColumn(name="participation_id", nullable=false)
	private Participation participation;

	@Column(name="start_time", nullable=false)
	private Date startTime;

	@Column(name="end_time", nullable=false)
	private Date endTime;

	public TimeBlock(){}
	
	public TimeBlock(TimeBlock t){
		this(t.getParticipation(), t.getStartTime(), t.getEndTime());
	}
	
	public TimeBlock(Participation p, Date start, Date end){
		this.participation = p;
		this.startTime = start == null? null : (Date)start.clone();
		this.endTime = end == null? null : (Date)end.clone();
	}

	public Map<String, Object> toJSONMap(){
		Map<String, Object> ret = new HashMap<String, Object>();
		Date truncDate = DateUtils.truncate(this.getStartTime(), Calendar.DAY_OF_MONTH);
		Date nextDate = DateUtils.addDays(truncDate, 1);
		
		ret.put("id", this.getId());
		ret.put("start", this.getStartTime().getTime());
		
		if (truncDate.equals(this.getStartTime()) && nextDate.equals(this.getEndTime())) {
			ret.put("allDay", true);
			ret.put("end", this.getStartTime().getTime());
		}
		else {
			ret.put("allDay", false);
			ret.put("end", this.getEndTime().getTime());
		}
		return ret;
	}

	public Long getId(){
		return this.id;
	}

	public void setId(Long id){
		this.id = id;
	}

	public Participation getParticipation(){
		return this.participation;
	}

	public void setParticipation(Participation participation){
		this.participation = participation;
	}

	public Date getStartTime(){
		return this.startTime;
	}

	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}

	public Date getEndTime(){
		return this.endTime;
	}

	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}

	public int compareTo(TimeBlock block){
		Date op1st = this.getStartTime();
		Date op1et = this.getEndTime();
		Date op2st = block.getStartTime();
		Date op2et = block.getEndTime();
		
		int comp = 0;
		if(op1st == null && op2st != null)
			return -1;
		if(op2st == null && op1st != null)
			return 1;
		
		if(op1et == null && op2et != null)
			return -1;
		if(op2et == null && op1et != null)
			return 1;
		
		if(op1st != null)
			comp = op1st.compareTo(op2st);
		if(op1et != null)
			comp = (comp == 0? op1et.compareTo(op2et) : comp);
		return comp;
	}

	public Long getDuration(){
		return this.getEndTime().getTime() - this.getStartTime().getTime();
	}
	
	public String toString(){
		return String.format("TimeBlock: {\n\tStart time: %d\n\tEnd time: %d\n}",
				this.getStartTime() == null? -1 : this.getStartTime().getTime(),
				this.getEndTime() == null? -1 : this.getEndTime().getTime());
	}
}
