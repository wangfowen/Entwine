package database.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="email_account")
public class EmailAccount {
	
	@Id
	private Long id;

	@Column(nullable=false)
	private String email;

	@OneToMany(mappedBy="participant")
	private List<Participation> participatingEventList;

	public EmailAccount() {}
	public EmailAccount(String email) {
		this.setEmail(email);
	}

	public Long getId(){
		return this.id;
	}

	public void setId(Long id){
		this.id = id;
	}

	public String getEmail(){
		return this.email;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public List<Participation> getParticipatingEventList(){
		if(this.participatingEventList == null)
			this.participatingEventList = new ArrayList<Participation>();
		return this.participatingEventList;
	}

	public void addParticipatingEventList(Participation participatingEvent){
		this.participatingEventList.add(participatingEvent);
		if(participatingEvent.getParticipant() != this)
			participatingEvent.setParticipant(this);
	}

	public void setParticipatingEventList(List<Participation> participatingEventList){
		this.participatingEventList = participatingEventList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EmailAccount) {
			EmailAccount acct = (EmailAccount) obj;
			if (acct.getId() == this.getId() && this.getEmail().equals(acct.getEmail()))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("EmailAccount: { Email: %s }", this.getEmail());
	}
	
}
