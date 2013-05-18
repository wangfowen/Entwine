package database.models;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.time.DateUtils;

@Entity
@Table(name="common_time_block")
public class CommonTimeBlock implements Comparable<CommonTimeBlock> {
	
	@Id
	private Long id;

	@Column(name="start_time", nullable=false)
	private Date startTime;

	@Column(name="end_time", nullable=false)
	private Date endTime;

	@ManyToOne
	@JoinColumn(name="event_id", nullable=false)
	private Event event;

	@ManyToMany
	@JoinTable(
		name = "ctb_account",
		joinColumns = {
			@JoinColumn(
				name = "ctb_id",
				referencedColumnName = "id"
			)
		},
		inverseJoinColumns = {
			@JoinColumn(
				name = "account_id",
				referencedColumnName = "id"
			)
		}
	)
	private List<EmailAccount> participants;

	public CommonTimeBlock() {}
	
	public CommonTimeBlock(TimeBlock tb) {
		this.setTimeBlock(tb);
	}
	
	public CommonTimeBlock(CommonTimeBlock ctb) {
		this.startTime =(Date) ctb.getStartTime().clone();
		this.endTime =(Date) ctb.getEndTime().clone();
		this.event = ctb.getEvent();
		List<EmailAccount> list = ctb.getParticipants();
		
		for(EmailAccount a : list)
			this.addParticipant(a);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	public void setTimeBlock(TimeBlock tb) {
		this.startTime =(Date) tb.getStartTime().clone();
		this.endTime =(Date) tb.getEndTime().clone();
	}

	public List<EmailAccount> getParticipants() {
		if(this.participants == null)
			this.participants = new LinkedList<EmailAccount>();
		return this.participants;
	}

	public void setParticipants(List<EmailAccount> participants) {
		this.participants = participants;
	}
	
	public void addParticipant(EmailAccount p) {
		this.getParticipants().add(p);
	}

	public Long getDuration(){
		return this.getEndTime().getTime() - this.getStartTime().getTime();
	}

	public int compareTo(CommonTimeBlock block){
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CommonTimeBlock) {
			CommonTimeBlock block = (CommonTimeBlock) obj;
			if (this.compareTo(block) == 0) {
				if (this.getParticipants().size() == block.getParticipants().size()) {
					if (block.getParticipants().containsAll(this.getParticipants()))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString(){
		return String.format("CommonTimeBlock: {\n\tStart time: %d\n\tEnd time: %d\n\tParticipants: %s\n}",
				this.getStartTime() == null? -1 : this.getStartTime().getTime(),
				this.getEndTime() == null? -1 : this.getEndTime().getTime(),
				this.getParticipants().toString());
	}
	
}
