package database.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.annotation.EnumValue;

@Entity
@Table(name="event")
public class Event {
	
	public enum Status {
		@EnumValue("1")
		NEW,
		@EnumValue("10")
		ACTIVE,
		@EnumValue("20")
		LOCKED,
		@EnumValue("30")
		FINALIZED,
		@EnumValue("40")
		INACTIVE
	}
	
	@Id
	private Long id;

	@Column(nullable=false)
	private String name;
	private String description;
	private String location;
	
	@Column(nullable=false)
	private Status status;

	@ManyToOne
	@JoinColumn(name="owner_id", nullable=false)
	private FullAccount owner;

	@Column(name="start_date")
	private Date startDate;

	@Column(name="end_date")
	private Date endDate;

	@Column(name="cutoff_date")
	private Date cutoffDate;

	@OneToMany(mappedBy="event")
	private List<Participation> participationList;

	@OneToMany(mappedBy="event")
	private List<CommonTimeBlock> commonTimeBlockList;

	public Event(){}

//	public String toJSONString(){
//		return String.format("{id:%d,name:%s,description:%s,location:%s,ownerId:%s,startDate:%s,endDate:%s,cutoffDate:%s}",
//				this.id, this.name, this.description == null? "null" : this.description, this.location == null? "null" : this.location,
//				this.owner.getId(), this.startDate == null? "null" : this.startDate.getTime(),
//				this.endDate == null? "null" : this.endDate.getTime(),
//				this.cutoffDate == null? "null" : this.cutoffDate.getTime());
//	}
	
	public String toString(){
		return String.format("Event: {\n\tName: %s\n\tDescription: %s\n\n\tOwner Id:%d\n}",
				this.getName(),
				this.getDescription(),
				this.getOwner().getId());
	}
	
	/*
	 * bean getters and setters
	 */

	public Long getId(){
		return this.id;
	}

	public void setId(Long id){
		this.id = id;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return this.description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getLocation(){
		return this.location;
	}

	public void setLocation(String location){
		this.location = location;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public FullAccount getOwner(){
		return this.owner;
	}

	public void setOwner(FullAccount owner){
		this.owner = owner;
	}

	public Date getStartDate(){
		return this.startDate;
	}

	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	public Date getEndDate(){
		return this.endDate;
	}

	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	public Date getCutoffDate(){
		return this.cutoffDate;
	}

	public void setCutoffDate(Date cutoffDate){
		this.cutoffDate = cutoffDate;
	}

	public List<Participation> getParticipationList(){
		if(this.participationList == null)
			this.participationList = new ArrayList<Participation>();
		return this.participationList;
	}

	public void setParticipationList(List<Participation> participationList){
		this.participationList = participationList;
	}

	public List<CommonTimeBlock> getCommonTimeBlockList(){
		if(this.commonTimeBlockList == null)
			this.commonTimeBlockList = new ArrayList<CommonTimeBlock>();
		return this.commonTimeBlockList;
	}

	public void setCommonTimeBlockList(List<CommonTimeBlock> commonTimeBlockList){
		this.commonTimeBlockList = commonTimeBlockList;
	}

	/*
	 * other getter and setter methods
	 */
	
	public Date getBestTimeBlock(){
		List<CommonTimeBlock> list = this.getCommonTimeBlockList();
		if(list.size() == 0)
			return null;
		return list.get(0).getStartTime();
	}

	public void addParticipation(Participation participation){
		participation.setEvent(this);
		this.getParticipationList().add(participation);
	}

	public void addParticipant(EmailAccount user){
		Participation p = new Participation();
		p.setEvent(this);
		p.setParticipant(user);
		p.setRole(0);
		p.setStatus(0);
		this.getParticipationList().add(p);
	}
	
	public void addCommonTimeBlock(CommonTimeBlock timeblock){
		timeblock.setEvent(this);
		this.getCommonTimeBlockList().add(timeblock);
	}
	
	public int getParticipationCount(){
		int count = 0;
		for(Participation p : this.getParticipationList()){
			if(p.hasSelectedAvailability())
				count ++;
		}
		
		return count;
	}
	
	public int getInvitedCount(){
		return this.getParticipationList().size();
	}
	
}
