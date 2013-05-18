package database.models

import javax.persistence._
import java.util._
import services.api.Security
import database.api.AccountsAPI

@Entity
@Table (name="full_account")
class FullAccount {

	@Id
	private var id: Long = _
	
	@Column
	private var fullname: String = null
	
	@Column
	private var alias: String = null
	
	@Column
	private var password: String = null
	
	@OneToOne
	@JoinColumn (name="email_account_id", nullable=false)
	private var emailAccount:EmailAccount = null
	
	@OneToMany (mappedBy="owner")
	private var createdEventList: List[Event] = null

	/**
	 * Setters
	 */
	def setId (id: Long) = this.id = id
	def setFullname (fullname: String) = this.fullname = fullname
	def setAlias (alias: String) = this.alias = alias
	def setPassword (password: String) = this.password = password
	def setEmailAccount (emailAccount: EmailAccount) = this.emailAccount = emailAccount
	def setCreatedEventList (cel: List[Event]) = {
		this.createdEventList = cel
	}
	
	/**
	 * Getters
	 */
	def getId (): Long = this.id
	def getFullname (): String = this.fullname
	def getAlias (): String = this.alias
	def getPassword (): String = this.password
	def getEmailAccount (): EmailAccount = this.emailAccount
	def getCreatedEventList (): List[Event] = {
		if(this.createdEventList == null)
			this.createdEventList = new LinkedList ()
		return this.createdEventList;
	}
	
	/**
	 * Helpers
	 */
	def hasCreatedEvents (): Boolean = !this.getCreatedEventList().isEmpty
	
	def getFormattedName (): String = {
		var fullname = this.getFullname()
		if (fullname == null || fullname.length() == 0)
			this.getEmailAccount.getEmail()
		else
			fullname
	}

	def addCreatedEvent (createdEvent: Event) = {
		this.getCreatedEventList().add(createdEvent)
		if(createdEvent.getOwner() != this)
			createdEvent.setOwner(this)
	}
	
	def comparePassword (password: String): Boolean = {
		if (this.getPassword().equals(password))
			true
		else
			false
	}
	
	def changeEmail (email: String) = {
		try
			Security.isValidEmailAddress(email)
		catch {
			case e: Exception =>
				throw e
		}
		
		if (email.equals(this.getEmailAccount().getEmail()))
			throw new IllegalArgumentException("Your new email is the same as the old email")
		
		this.getEmailAccount().setEmail(email)
		AccountsAPI.updateEmailAccount(this.getEmailAccount())
	}
	
	def changePassword (password: String) = {
		try
			Security.isValidPassword(password)
		catch {
			case e: Exception =>
				throw e
		}
		
		if (password.equals(this.getPassword()))
			throw new IllegalArgumentException("Your new password is the same as the old password")
		
		this.setPassword(password)
		AccountsAPI.updateFullAccount(this)
	}
	
}