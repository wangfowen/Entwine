package database.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import database.api.ParticipationsAPI;

@Entity
@Table(name="participation")
public class Participation {
	
	@Id
	private Long id;
	private Integer status;
	private Integer role;

	@ManyToOne
	@JoinColumn(name="participant_id", nullable=false)
	private EmailAccount participant;

	@ManyToOne
	@JoinColumn(name="event_id", nullable=false)
	private Event event;

	@OneToMany(mappedBy="participation")
	private List<TimeBlock> timeBlocks;

	public Participation(){}
	
	public Participation(EmailAccount user, Event event){
		this.participant = user;
		this.event = event;
	}

	public Map<String, Object> toJSONMap(){
		Map<String, Object> ret = new HashMap<String, Object>();
		List<Map<String, Object>> tbList = new ArrayList<Map<String, Object>>();
		
		if(this.getTimeBlocks() != null)
			for(TimeBlock tb : this.getTimeBlocks())
				tbList.add(tb.toJSONMap());
		
		ret.put("id", this.getId());
		ret.put("timeBlocks", tbList);
		ret.put("role", this.getRole());
		ret.put("status", this.getStatus());
		
		return ret;
	}

	public Long getId(){
		return this.id;
	}

	public void setId(Long id){
		this.id = id;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getRole(){
		return this.role;
	}

	public void setRole(Integer role){
		this.role = role;
	}

	public EmailAccount getParticipant(){
		return this.participant;
	}

	public void setParticipant(EmailAccount participant){
		this.participant = participant;
	}

	public Event getEvent(){
		return this.event;
	}

	public void setEvent(Event event){
		this.event = event;
	}

	public List<TimeBlock> getTimeBlocks(){
		return this.timeBlocks;
	}

	public void addTimeBlock(TimeBlock timeblock) {
		if (this.getTimeBlocks() == null)
			this.timeBlocks = new ArrayList<TimeBlock>();
		for (TimeBlock tb : this.getTimeBlocks()) {
			if (timeblock.getId() != null && tb.getId().equals(timeblock.getId()))
				return;
		}
		this.timeBlocks.add(timeblock);
		timeblock.setParticipation(this);
	}

	public void setTimeBlocks(List<TimeBlock> timeBlocks) {
		this.timeBlocks = timeBlocks;
	}
	
	public boolean hasSelectedAvailability() {
		return this.getTimeBlocks().size() != 0;
	}
	
	public void removeTimeBlock(Long tbid) {
		TimeBlock tb = ParticipationsAPI.deleteTimeBlock(tbid);
		if (tb != null && this.getTimeBlocks() != null)
			this.getTimeBlocks().remove(tb);
	}
	
}
